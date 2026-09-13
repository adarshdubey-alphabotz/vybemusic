package com.alphabotz.vybemusic.core.network

import android.util.Base64
import android.util.Log
import com.alphabotz.vybemusic.core.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object VybeMusicEngine {
    private const val TAG = "VybeMusicEngine"
    private const val DES_KEY = "38346591"
    private const val BASE_URL = "https://www.jiosaavn.com/api.php"

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Decrypts JioSaavn encrypted_media_url to direct CDN 320kbps/160kbps audio stream URL
     */
    fun decryptMediaUrl(encryptedUrl: String): String {
        return try {
            val keyBytes = DES_KEY.toByteArray(Charsets.UTF_8)
            val secretKey = SecretKeySpec(keyBytes, "DES")
            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decodedBytes = Base64.decode(encryptedUrl, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            val decryptedUrl = String(decryptedBytes, Charsets.UTF_8)
            decryptedUrl.replace("_96.mp4", "_320.mp4")
                .replace("_96.m4a", "_320.mp4")
        } catch (e: Exception) {
            Log.e(TAG, "Decryption error for $encryptedUrl", e)
            ""
        }
    }

    /**
     * Instant seed tracks displayed in 0ms so the user never sees an empty screen
     */
    fun getInitialSeedTracks(): List<Track> {
        return listOf(
            Track(
                id = "J1ejXBn3",
                title = "Starboy",
                artist = "The Weeknd, Daft Punk",
                album = "Starboy",
                durationSeconds = 231,
                artworkUrl = "https://c.saavncdn.com/320/Starboy-Tamil-2023-20240404043449-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/320/f7bd814ccabdbe44b0b5952e6adda39b_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "English"
            ),
            Track(
                id = "rjkrTnma",
                title = "Kesariya",
                artist = "Arijit Singh, Pritam",
                album = "Brahmastra",
                durationSeconds = 268,
                artworkUrl = "https://c.saavncdn.com/871/Brahmastra-Original-Motion-Picture-Soundtrack-Hindi-2022-20221006155213-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/871/c2febd353f3a076a406fa37510f31f9f_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "Hindi"
            ),
            Track(
                id = "PfcG4gJU",
                title = "Excuses",
                artist = "AP Dhillon, Gurinder Gill",
                album = "Excuses",
                durationSeconds = 176,
                artworkUrl = "https://c.saavncdn.com/890/Excuses-English-2021-20210930112054-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "Punjabi"
            ),
            Track(
                id = "37a12qrO",
                title = "Sunflower",
                artist = "Post Malone, Swae Lee",
                album = "Spider-Man: Into the Spider-Verse",
                durationSeconds = 158,
                artworkUrl = "https://c.saavncdn.com/156/ZZang-KARAOKE-2024-POP-Vol-48-Instrumental-2024-20260807162307-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/156/4b8d33808605485d7ec127b96141a7ae_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "English"
            ),
            Track(
                id = "MpioDMVf",
                title = "Shape of You",
                artist = "Ed Sheeran",
                album = "Divide",
                durationSeconds = 233,
                artworkUrl = "https://c.saavncdn.com/551/Sakura-Sakura-Best-Violin-Instrumental-2025-20250706140133-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/551/b790d860770f04780e5fe745a60521e3_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "English"
            ),
            Track(
                id = "CEW6mQiQ",
                title = "Blinding Lights",
                artist = "The Weeknd",
                album = "After Hours",
                durationSeconds = 200,
                artworkUrl = "https://c.saavncdn.com/809/Mainstream-Overtures-Instrumental-2026-20260505080135-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/809/4805bce54d5079d05c8eba3678b27a35_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "English"
            ),
            Track(
                id = "8GjYgd0S",
                title = "Levitating",
                artist = "Dua Lipa",
                album = "Future Nostalgia",
                durationSeconds = 203,
                artworkUrl = "https://c.saavncdn.com/405/Beb-Roca-xitos-Populares-Sesiones-de-Sue-o-Zen-Piano-Instrumental-Version-Instrumental-2026-20260218165802-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/405/634f4951f42a6ca436bacb3d6ccc8d65_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "English"
            ),
            Track(
                id = "idS5a57X",
                title = "Stay",
                artist = "The Kid LAROI, Justin Bieber",
                album = "F*CK LOVE 3",
                durationSeconds = 141,
                artworkUrl = "https://c.saavncdn.com/543/ZZang-KARAOKE-Greatest-POP-Vol-9-Instrumental-2024-20260120071413-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/543/960a993e6eeedfa63ad9efff2f94b01a_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "English"
            )
        )
    }

    /**
     * Searches tracks across JioSaavn 100M+ catalog with 320kbps audio streams
     */
    suspend fun searchTracks(query: String, page: Int = 1, limit: Int = 20): List<Track> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "$BASE_URL?__call=search.getResults&_format=json&_marker=0&api_version=4&ctx=web6dot0&q=$encodedQuery&p=$page&n=$limit"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile)")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val body = response.body?.string() ?: return@withContext emptyList()
                val json = JSONObject(body)
                val results = json.optJSONArray("results") ?: return@withContext emptyList()

                parseTrackList(results)
            }
        } catch (e: Exception) {
            Log.e(TAG, "searchTracks error for $query", e)
            emptyList()
        }
    }

    /**
     * Fetches Top Trending Songs with live fallback
     */
    suspend fun getTrendingTracks(genreOrMood: String = "trending"): List<Track> = withContext(Dispatchers.IO) {
        val query = when (genreOrMood.lowercase()) {
            "⚡ energetic", "workout" -> "workout energetic hits"
            "🧠 focus / study", "focus" -> "lofi chill study beats"
            "🌙 night drive" -> "night drive synthwave phonk"
            "🎧 lo-fi" -> "lofi hip hop relaxing"
            "🧘 relax" -> "relaxing acoustic peaceful"
            "new release" -> "latest new english hits"
            else -> "top billboard trending hits"
        }

        val networkTracks = searchTracks(query, page = 1, limit = 25)
        if (networkTracks.isNotEmpty()) {
            networkTracks
        } else {
            getInitialSeedTracks()
        }
    }

    private fun parseTrackList(array: JSONArray): List<Track> {
        val tracks = mutableListOf<Track>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val track = parseTrackJson(obj)
            if (track != null) {
                tracks.add(track)
            }
        }
        return tracks
    }

    private fun parseTrackJson(obj: JSONObject): Track? {
        return try {
            val id = obj.optString("id").takeIf { it.isNotBlank() } ?: return null
            val rawTitle = obj.optString("title", "Unknown Track")
            val title = cleanHtml(rawTitle)

            val moreInfo = obj.optJSONObject("more_info") ?: JSONObject()

            val subtitle = obj.optString("subtitle")
            val singers = moreInfo.optString("singers")
            val artistMap = moreInfo.optJSONObject("artistMap")
            val primaryArtistsArray = artistMap?.optJSONArray("primary_artists")
            val primaryArtistNames = if (primaryArtistsArray != null && primaryArtistsArray.length() > 0) {
                val names = mutableListOf<String>()
                for (j in 0 until primaryArtistsArray.length()) {
                    val aObj = primaryArtistsArray.optJSONObject(j)
                    val aName = aObj?.optString("name")
                    if (!aName.isNullOrBlank()) names.add(aName)
                }
                names.joinToString(", ")
            } else null

            val artist = cleanHtml(
                primaryArtistNames
                    ?: singers.takeIf { it.isNotBlank() }
                    ?: subtitle.takeIf { it.isNotBlank() }
                    ?: "Various Artists"
            )

            val album = cleanHtml(moreInfo.optString("album", "Vybe Single"))
            val durationSecs = moreInfo.optString("duration", "0").toLongOrNull() ?: 0L

            val rawImage = obj.optString("image", "")
            val artworkUrl = rawImage.replace("150x150", "500x500")
                .replace("50x50", "500x500")
                .replace("http://", "https://")

            val encMediaUrl = moreInfo.optString("encrypted_media_url", "")
            val streamUrl = if (encMediaUrl.isNotBlank()) {
                decryptMediaUrl(encMediaUrl)
            } else {
                moreInfo.optString("vlink", "")
            }

            if (streamUrl.isBlank()) return null

            Track(
                id = id,
                title = title,
                artist = artist,
                album = album,
                durationSeconds = durationSecs,
                artworkUrl = artworkUrl,
                streamUrl = streamUrl,
                audioQuality = "320kbps Lossless AAC",
                language = obj.optString("language", "English")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun cleanHtml(str: String): String {
        return str.replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&#039;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&apos;", "'")
            .trim()
    }
}
