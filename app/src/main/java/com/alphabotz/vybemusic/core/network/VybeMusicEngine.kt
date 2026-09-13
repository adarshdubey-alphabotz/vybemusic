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

    private val BAD_KEYWORDS = listOf(
        "karaoke",
        "instrumental",
        "originally perfomed",
        "tribute to",
        "baby sleep",
        "marimba",
        "piano version",
        "ringtone"
    )

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

    fun getInitialSeedTracks(): List<Track> {
        return listOf(
            Track(
                id = "rjkrTnma",
                title = "Kesariya",
                artist = "Pritam, Arijit Singh, Amitabh Bhattacharya - Brahmastra",
                album = "Brahmastra",
                durationSeconds = 268L,
                artworkUrl = "https://c.saavncdn.com/871/Brahmastra-Original-Motion-Picture-Soundtrack-Hindi-2022-20221006155213-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/871/c2febd353f3a076a406fa37510f31f9f_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "Hindi/English"
            ),
            Track(
                id = "PfcG4gJU",
                title = "Excuses",
                artist = "AP Dhillon, Gurinder Gill, Intense - Excuses",
                album = "Excuses",
                durationSeconds = 177L,
                artworkUrl = "https://c.saavncdn.com/890/Excuses-English-2021-20210930112054-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "Hindi/English"
            ),
            Track(
                id = "1e0En7YX",
                title = "Pehle Bhi Main",
                artist = "Vishal Mishra, Raj Shekhar - ANIMAL",
                album = "ANIMAL",
                durationSeconds = 250L,
                artworkUrl = "https://c.saavncdn.com/092/ANIMAL-Hindi-2023-20260724191152-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/092/81b52beea90f186f27cf5c5eead972c8_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "Hindi/English"
            ),
            Track(
                id = "qZtKBMZ_",
                title = "Apna Bana Le",
                artist = "Sachin-Jigar, Arijit Singh - Bhediya",
                album = "Bhediya",
                durationSeconds = 261L,
                artworkUrl = "https://c.saavncdn.com/815/Bhediya-Hindi-2023-20230927155213-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/815/483a6e118e8108cbb3e5cd8701674f32_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "Hindi/English"
            ),
            Track(
                id = "faloMmjX",
                title = "Chaleya",
                artist = "Anirudh Ravichander, Arijit Singh, Shilpa Rao - Jawan",
                album = "Jawan",
                durationSeconds = 200L,
                artworkUrl = "https://c.saavncdn.com/047/Jawan-Hindi-2023-20230921190854-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/047/d1366530468931703ac909e82a3ee788_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "Hindi/English"
            ),
            Track(
                id = "M7k5t7vw",
                title = "Lover",
                artist = "Diljit Dosanjh - MoonChild Era",
                album = "MoonChild Era",
                durationSeconds = 190L,
                artworkUrl = "https://c.saavncdn.com/209/MoonChild-Era-Punjabi-2021-20240715073449-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/209/88cd9a1cc0af8768d67272876bb09851_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "Hindi/English"
            ),
            Track(
                id = "pyJaNwrF",
                title = "Faded",
                artist = "Veronica Bravo, Le Bober, Deep Mage - Faded",
                album = "Faded",
                durationSeconds = 165L,
                artworkUrl = "https://c.saavncdn.com/670/Faded-Instrumental-2022-20260324143104-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/670/31d24c80462a61591ceabf81d1c749ae_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "Hindi/English"
            ),
            Track(
                id = "J1ejXBn3",
                title = "Starboy",
                artist = "vaarairuthi - Starboy",
                album = "Starboy",
                durationSeconds = 236L,
                artworkUrl = "https://c.saavncdn.com/320/Starboy-Tamil-2023-20240404043449-500x500.jpg",
                streamUrl = "https://aac.saavncdn.com/320/f7bd814ccabdbe44b0b5952e6adda39b_320.mp4",
                audioQuality = "320kbps Lossless AAC",
                language = "Hindi/English"
            )
        )
    }

    suspend fun searchTracks(query: String, page: Int = 1, limit: Int = 25): List<Track> = withContext(Dispatchers.IO) {
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

    suspend fun getTrendingTracks(genreOrMood: String = "trending"): List<Track> = withContext(Dispatchers.IO) {
        val query = when (genreOrMood.lowercase()) {
            "⚡ new release", "new release" -> "latest bollywood punjabi hits"
            "🔥 trending", "trending" -> "top trending hindi english hits"
            "🎧 lo-fi", "lofi" -> "lofi hindi acoustic chill"
            "🌙 night drive" -> "night drive phonk beats"
            "🧠 focus", "focus" -> "deep focus study chill"
            else -> "top bollywood trending songs"
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

            // Filter out junk / karaoke / covers
            val lowerTitle = title.lowercase()
            val lowerArtist = artist.lowercase()
            if (BAD_KEYWORDS.any { lowerTitle.contains(it) || lowerArtist.contains(it) }) {
                return null
            }

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
                language = obj.optString("language", "Hindi/English")
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
