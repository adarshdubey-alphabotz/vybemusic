package com.alphabotz.vybemusic.core.network

import com.alphabotz.vybemusic.core.model.Lyrics
import com.alphabotz.vybemusic.core.model.LyricsLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

object LrclibLyricsApi {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Fetches synchronized karaoke lyrics for a track by title and artist.
     */
    suspend fun getLyrics(trackId: String, title: String, artist: String): Lyrics? = withContext(Dispatchers.IO) {
        try {
            val encodedTitle = URLEncoder.encode(title.trim(), "UTF-8")
            val encodedArtist = URLEncoder.encode(artist.trim(), "UTF-8")
            val url = "https://lrclib.net/api/get?track_name=$encodedTitle&artist_name=$encodedArtist"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "VybeMusic/1.0.0 (https://github.com/adarshdubey-alphabotz/vybemusic)")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                val jsonObject = json.parseToJsonElement(body).jsonObject

                val syncedLyricsRaw = jsonObject["syncedLyrics"]?.jsonPrimitive?.content
                val plainLyrics = jsonObject["plainLyrics"]?.jsonPrimitive?.content ?: ""

                if (!syncedLyricsRaw.isNullOrBlank()) {
                    val parsedLines = parseLrc(syncedLyricsRaw)
                    return@withContext Lyrics(
                        trackId = trackId,
                        isSynced = true,
                        plainLyrics = plainLyrics,
                        lines = parsedLines
                    )
                } else if (plainLyrics.isNotBlank()) {
                    return@withContext Lyrics(
                        trackId = trackId,
                        isSynced = false,
                        plainLyrics = plainLyrics,
                        lines = emptyList()
                    )
                }
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Parses LRC string format: [01:23.45] lyric text here
     */
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
