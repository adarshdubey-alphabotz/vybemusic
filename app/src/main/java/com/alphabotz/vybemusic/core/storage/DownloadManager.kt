package com.alphabotz.vybemusic.core.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.network.VybeMusicEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object DownloadManager {
    private const val TAG = "DownloadManager"
    private const val PREFS_NAME = "vybe_downloads_prefs"
    private const val KEY_DOWNLOADED_TRACKS = "downloaded_tracks_json"

    private val json = Json { ignoreUnknownKeys = true }
    private var prefs: SharedPreferences? = null
    private var appContext: Context? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val _downloadedTracks = MutableStateFlow<List<Track>>(emptyList())
    val downloadedTracks: StateFlow<List<Track>> = _downloadedTracks.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress.asStateFlow()

    fun init(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadDownloadedTracks()
        }
    }

    private fun loadDownloadedTracks() {
        val raw = prefs?.getString(KEY_DOWNLOADED_TRACKS, null) ?: return
        try {
            val list = json.decodeFromString<List<Track>>(raw)
            _downloadedTracks.value = list
        } catch (e: Exception) {
            _downloadedTracks.value = emptyList()
        }
    }

    private fun saveDownloadedTracks() {
        try {
            val raw = json.encodeToString(_downloadedTracks.value)
            prefs?.edit()?.putString(KEY_DOWNLOADED_TRACKS, raw)?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save downloaded tracks", e)
        }
    }

    fun isDownloaded(trackId: String): Boolean {
        return _downloadedTracks.value.any { it.id == trackId }
    }

    fun isDownloading(trackId: String): Boolean {
        return _downloadProgress.value.containsKey(trackId)
    }

    fun downloadTrack(track: Track, onComplete: ((Boolean) -> Unit)? = null) {
        if (isDownloaded(track.id) || isDownloading(track.id)) {
            onComplete?.invoke(true)
            return
        }

        val context = appContext ?: return
        scope.launch {
            try {
                _downloadProgress.value = _downloadProgress.value + (track.id to 0.05f)

                val streamUrl = VybeMusicEngine.resolveStreamUrl(track)
                if (streamUrl.isBlank()) {
                    _downloadProgress.value = _downloadProgress.value - track.id
                    withContext(Dispatchers.Main) { onComplete?.invoke(false) }
                    return@launch
                }

                val downloadsDir = File(context.filesDir, "downloads").apply { if (!exists()) mkdirs() }
                val safeFileName = "track_" + track.id.replace(Regex("[^a-zA-Z0-9_]"), "_") + ".mp3"
                val destFile = File(downloadsDir, safeFileName)

                val request = Request.Builder().url(streamUrl).header("User-Agent", "Mozilla/5.0").build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful || response.body == null) {
                    _downloadProgress.value = _downloadProgress.value - track.id
                    withContext(Dispatchers.Main) { onComplete?.invoke(false) }
                    return@launch
                }

                val body = response.body!!
                val contentLength = body.contentLength()
                val inputStream = body.byteStream()
                val outputStream = FileOutputStream(destFile)

                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytesRead = 0L

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    if (contentLength > 0) {
                        val progress = (totalBytesRead.toFloat() / contentLength).coerceIn(0.1f, 0.99f)
                        _downloadProgress.value = _downloadProgress.value + (track.id to progress)
                    }
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                val localTrack = track.copy(
                    streamUrl = destFile.absolutePath,
                    audioQuality = "320kbps Offline Master"
                )

                _downloadedTracks.value = _downloadedTracks.value + localTrack
                saveDownloadedTracks()
                _downloadProgress.value = _downloadProgress.value - track.id

                withContext(Dispatchers.Main) {
                    onComplete?.invoke(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Download failed for " + track.title, e)
                _downloadProgress.value = _downloadProgress.value - track.id
                withContext(Dispatchers.Main) {
                    onComplete?.invoke(false)
                }
            }
        }
    }

    fun deleteDownloadedTrack(trackId: String) {
        val list = _downloadedTracks.value.toMutableList()
        val item = list.find { it.id == trackId }
        if (item != null) {
            try {
                val file = File(item.streamUrl)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                // ignore
            }
            list.remove(item)
            _downloadedTracks.value = list
            saveDownloadedTracks()
        }
    }
}
