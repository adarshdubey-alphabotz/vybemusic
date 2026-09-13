package com.alphabotz.vybemusic.core.network

import android.util.Log
import com.alphabotz.vybemusic.core.model.Lyrics
import com.alphabotz.vybemusic.core.model.LyricsLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object LrclibLyricsApi {
    private const val TAG = "LrclibLyricsApi"
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    /**
     * Fetches synchronized karaoke lyrics for a track by search query or exact match
     */
    suspend fun getLyrics(trackId: String, title: String, artist: String): Lyrics? = withContext(Dispatchers.IO) {
        try {
            val cleanTitle = cleanSearchTerm(title)
            val cleanArtist = cleanSearchTerm(artist.split(",", "-", "&").firstOrNull() ?: artist)
            val query = "$cleanTitle $cleanArtist".trim()

            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val searchUrl = "https://lrclib.net/api/search?q=$encodedQuery"

            val request = Request.Builder()
                .url(searchUrl)
                .header("User-Agent", "VybeMusic/1.0.0 (Android)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank() && body.startsWith("[")) {
                        val array = JSONArray(body)
                        for (i in 0 until array.length()) {
                            val item = array.optJSONObject(i) ?: continue
                            val syncedRaw = item.optString("syncedLyrics", "")
                            val plainRaw = item.optString("plainLyrics", "")

                            if (syncedRaw.isNotBlank()) {
                                val lines = parseLrc(syncedRaw)
                                if (lines.isNotEmpty()) {
                                    return@withContext Lyrics(
                                        trackId = trackId,
                                        isSynced = true,
                                        plainLyrics = plainRaw,
                                        lines = lines
                                    )
                                }
                            } else if (plainRaw.isNotBlank()) {
                                return@withContext Lyrics(
                                    trackId = trackId,
                                    isSynced = false,
                                    plainLyrics = plainRaw,
                                    lines = emptyList()
                                )
                            }
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Lyrics fetch error for $title", e)
            null
        }
    }

    private fun cleanSearchTerm(term: String): String {
        return term.replace(Regex("""\(.*?\)"""), "")
            .replace(Regex("""\[.*?\]"""), "")
            .replace(Regex("[^a-zA-Z0-9 ]"), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun parseLrc(lrcContent: String): List<LyricsLine> {
        val lines = mutableListOf<LyricsLine>()
        val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2,3})\](.*)""")

        lrcContent.lines().forEach { line ->
            val match = regex.find(line.trim())
            if (match != null) {
                val min = match.groupValues[1].toLongOrNull() ?: 0L
                val sec = match.groupValues[2].toLongOrNull() ?: 0L
                val msStr = match.groupValues[3]
                val ms = if (msStr.length == 2) msStr.toLong() * 10 else msStr.toLong()

                val totalMs = (min * 60 * 1000) + (sec * 1000) + ms
                val text = match.groupValues[4].trim()

                if (text.isNotEmpty()) {
                    lines.add(LyricsLine(startTimeMs = totalMs, text = text))
                }
            }
        }
        return lines.sortedBy { it.startTimeMs }
    }
}
