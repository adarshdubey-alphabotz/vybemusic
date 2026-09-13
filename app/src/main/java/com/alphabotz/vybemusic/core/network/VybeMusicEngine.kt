package com.alphabotz.vybemusic.core.network

import android.util.Base64
import android.util.Log
import com.alphabotz.vybemusic.core.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    val MASTER_CURATED_CATALOG: List<Track> = listOf(
        // === 2Pac / Hip-Hop Legends ===
        Track(
            id = "2pac_hit_em_up",
            title = "Hit 'Em Up",
            artist = "2Pac, Outlawz",
            album = "Greatest Hits",
            durationSeconds = 312L,
            artworkUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),
        Track(
            id = "2pac_california_love",
            title = "California Love",
            artist = "2Pac, Dr. Dre, Roger Troutman",
            album = "All Eyez On Me",
            durationSeconds = 285L,
            artworkUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),
        Track(
            id = "2pac_changes",
            title = "Changes",
            artist = "2Pac, Talent",
            album = "Greatest Hits",
            durationSeconds = 269L,
            artworkUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),
        Track(
            id = "2pac_all_eyez",
            title = "All Eyez On Me",
            artist = "2Pac, Big Syke",
            album = "All Eyez On Me",
            durationSeconds = 308L,
            artworkUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),

        // === The Weeknd ===
        Track(
            id = "weeknd_starboy",
            title = "Starboy",
            artist = "The Weeknd, Daft Punk",
            album = "Starboy",
            durationSeconds = 230L,
            artworkUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),
        Track(
            id = "weeknd_blinding",
            title = "Blinding Lights",
            artist = "The Weeknd",
            album = "After Hours",
            durationSeconds = 200L,
            artworkUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),

        // === Charlie Puth ===
        Track(
            id = "charlie_how_long",
            title = "How Long",
            artist = "Charlie Puth",
            album = "Voicenotes",
            durationSeconds = 198L,
            artworkUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),
        Track(
            id = "charlie_attention",
            title = "Attention",
            artist = "Charlie Puth",
            album = "Voicenotes",
            durationSeconds = 208L,
            artworkUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),

        // === Eminem ===
        Track(
            id = "eminem_mockingbird",
            title = "Mockingbird",
            artist = "Eminem",
            album = "Encore",
            durationSeconds = 250L,
            artworkUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),
        Track(
            id = "eminem_lose_yourself",
            title = "Lose Yourself",
            artist = "Eminem",
            album = "8 Mile",
            durationSeconds = 326L,
            artworkUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),

        // === Travis Scott ===
        Track(
            id = "travis_fein",
            title = "FE!N",
            artist = "Travis Scott, Playboi Carti",
            album = "UTOPIA",
            durationSeconds = 191L,
            artworkUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),
        Track(
            id = "travis_goosebumps",
            title = "Goosebumps",
            artist = "Travis Scott, Kendrick Lamar",
            album = "Birds In The Trap Sing McKnight",
            durationSeconds = 243L,
            artworkUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),

        // === Wiz Khalifa ===
        Track(
            id = "wiz_black_yellow",
            title = "Black and Yellow",
            artist = "Wiz Khalifa",
            album = "Rolling Papers",
            durationSeconds = 217L,
            artworkUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "English"
        ),

        // === AP Dhillon & Punjabi Chartbusters ===
        Track(
            id = "PfcG4gJU",
            title = "Excuses",
            artist = "AP Dhillon, Gurinder Gill, Intense",
            album = "Excuses",
            durationSeconds = 177L,
            artworkUrl = "https://c.saavncdn.com/890/Excuses-English-2021-20210930112054-500x500.jpg",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "Punjabi"
        ),
        Track(
            id = "ap_brown_munde",
            title = "Brown Munde",
            artist = "AP Dhillon, Gurinder Gill, Shinda Kahlon",
            album = "Not by Chance",
            durationSeconds = 268L,
            artworkUrl = "https://c.saavncdn.com/890/Excuses-English-2021-20210930112054-500x500.jpg",
            streamUrl = "https://aac.saavncdn.com/890/a18aabc4681dc6c334d5d29b67e84a0f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "Punjabi"
        ),
        Track(
            id = "sidhu_295",
            title = "295",
            artist = "Sidhu Moose Wala",
            album = "Moosetape",
            durationSeconds = 270L,
            artworkUrl = "https://c.saavncdn.com/209/MoonChild-Era-Punjabi-2021-20240715073449-500x500.jpg",
            streamUrl = "https://aac.saavncdn.com/209/88cd9a1cc0af8768d67272876bb09851_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "Punjabi"
        ),
        Track(
            id = "M7k5t7vw",
            title = "Lover",
            artist = "Diljit Dosanjh",
            album = "MoonChild Era",
            durationSeconds = 190L,
            artworkUrl = "https://c.saavncdn.com/209/MoonChild-Era-Punjabi-2021-20240715073449-500x500.jpg",
            streamUrl = "https://aac.saavncdn.com/209/88cd9a1cc0af8768d67272876bb09851_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "Punjabi"
        ),

        // === Bollywood & Melodies ===
        Track(
            id = "rjkrTnma",
            title = "Kesariya",
            artist = "Pritam, Arijit Singh, Amitabh Bhattacharya",
            album = "Brahmastra",
            durationSeconds = 268L,
            artworkUrl = "https://c.saavncdn.com/871/Brahmastra-Original-Motion-Picture-Soundtrack-Hindi-2022-20221006155213-500x500.jpg",
            streamUrl = "https://aac.saavncdn.com/871/c2febd353f3a076a406fa37510f31f9f_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "Hindi"
        ),
        Track(
            id = "1e0En7YX",
            title = "Pehle Bhi Main",
            artist = "Vishal Mishra, Raj Shekhar",
            album = "ANIMAL",
            durationSeconds = 250L,
            artworkUrl = "https://c.saavncdn.com/092/ANIMAL-Hindi-2023-20260724191152-500x500.jpg",
            streamUrl = "https://aac.saavncdn.com/092/81b52beea90f186f27cf5c5eead972c8_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "Hindi"
        ),
        Track(
            id = "qZtKBMZ_",
            title = "Apna Bana Le",
            artist = "Sachin-Jigar, Arijit Singh",
            album = "Bhediya",
            durationSeconds = 261L,
            artworkUrl = "https://c.saavncdn.com/815/Bhediya-Hindi-2023-20230927155213-500x500.jpg",
            streamUrl = "https://aac.saavncdn.com/815/483a6e118e8108cbb3e5cd8701674f32_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "Hindi"
        ),
        Track(
            id = "faloMmjX",
            title = "Chaleya",
            artist = "Anirudh Ravichander, Arijit Singh, Shilpa Rao",
            album = "Jawan",
            durationSeconds = 200L,
            artworkUrl = "https://c.saavncdn.com/047/Jawan-Hindi-2023-20230921190854-500x500.jpg",
            streamUrl = "https://aac.saavncdn.com/047/d1366530468931703ac909e82a3ee788_320.mp4",
            audioQuality = "320kbps Lossless AAC",
            language = "Hindi"
        )
    )

    fun getInitialSeedTracks(): List<Track> {
        return MASTER_CURATED_CATALOG
    }

    suspend fun getCuratedFeedForArtists(favoriteArtists: Set<String>): List<Track> = withContext(Dispatchers.IO) {
        if (favoriteArtists.isEmpty()) {
            return@withContext MASTER_CURATED_CATALOG
        }

        // Match from curated master catalog
        val matchedTracks = MASTER_CURATED_CATALOG.filter { track ->
            favoriteArtists.any { artist ->
                track.artist.contains(artist, ignoreCase = true) ||
                        track.title.contains(artist, ignoreCase = true)
            }
        }.toMutableList()

        // Also query API concurrently for any artists that have fewer matches
        val queryArtists = favoriteArtists.take(3)
        val apiDeferred = queryArtists.map { artistName ->
            async {
                searchTracks(artistName, limit = 8)
            }
        }
        val apiResults = apiDeferred.awaitAll().flatten()

        for (t in apiResults) {
            if (matchedTracks.none { it.id == t.id || it.title.equals(t.title, ignoreCase = true) }) {
                matchedTracks.add(t)
            }
        }

        if (matchedTracks.isEmpty()) {
            MASTER_CURATED_CATALOG
        } else {
            matchedTracks.shuffled()
        }
    }

    suspend fun getSimilarTracks(track: Track): List<Track> = withContext(Dispatchers.IO) {
        val artistLower = track.artist.lowercase()
        val langLower = track.language.lowercase()

        // 1. If Hip-Hop / Western Rap (2Pac, Eminem, Travis, Drake, Wiz)
        if (artistLower.contains("2pac") || artistLower.contains("tupac") ||
            artistLower.contains("eminem") || artistLower.contains("travis") ||
            artistLower.contains("weeknd") || artistLower.contains("charlie") ||
            artistLower.contains("wiz") || langLower.contains("english")
        ) {
            val hipHopTracks = MASTER_CURATED_CATALOG.filter {
                it.id != track.id && it.language.equals("English", ignoreCase = true)
            }
            if (hipHopTracks.isNotEmpty()) return@withContext hipHopTracks.shuffled()
        }

        // 2. If Punjabi (AP Dhillon, Sidhu, Diljit, Aujla)
        if (artistLower.contains("ap dhillon") || artistLower.contains("sidhu") ||
            artistLower.contains("diljit") || artistLower.contains("karan aujla") ||
            langLower.contains("punjabi")
        ) {
            val punjabiTracks = MASTER_CURATED_CATALOG.filter {
                it.id != track.id && it.language.equals("Punjabi", ignoreCase = true)
            }
            if (punjabiTracks.isNotEmpty()) return@withContext punjabiTracks.shuffled()
        }

        // 3. If Bollywood / Hindi
        val hindiTracks = MASTER_CURATED_CATALOG.filter {
            it.id != track.id && it.language.equals("Hindi", ignoreCase = true)
        }
        if (hindiTracks.isNotEmpty()) return@withContext hindiTracks.shuffled()

        // Fallback
        MASTER_CURATED_CATALOG.filter { it.id != track.id }.shuffled()
    }

    suspend fun searchTracks(query: String, page: Int = 1, limit: Int = 25): List<Track> = withContext(Dispatchers.IO) {
        try {
            // Check local curated catalog first for instant response
            val localMatches = MASTER_CURATED_CATALOG.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true) ||
                        it.album.contains(query, ignoreCase = true)
            }

            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "$BASE_URL?__call=search.getResults&_format=json&_marker=0&api_version=4&ctx=web6dot0&q=$encodedQuery&p=$page&n=$limit"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile)")
                .build()

            val networkTracks = client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use emptyList()
                val body = response.body?.string() ?: return@use emptyList()
                val json = JSONObject(body)
                val results = json.optJSONArray("results") ?: return@use emptyList()

                parseTrackList(results)
            }

            val combined = (localMatches + networkTracks).distinctBy { it.id }
            if (combined.isNotEmpty()) combined else localMatches
        } catch (e: Exception) {
            Log.e(TAG, "searchTracks error for $query", e)
            MASTER_CURATED_CATALOG.filter {
                it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
            }
        }
    }

    suspend fun getTrendingTracks(genreOrMood: String = "trending"): List<Track> = withContext(Dispatchers.IO) {
        val query = when (genreOrMood.lowercase()) {
            "⚡ new release", "new release" -> "latest top hits"
            "🔥 trending", "trending" -> "top trending songs"
            "🎧 lo-fi", "lofi" -> "lofi chill study"
            "🌙 night drive" -> "night drive phonk beats"
            "🧠 focus", "focus" -> "deep focus ambient"
            else -> "top hits songs"
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
