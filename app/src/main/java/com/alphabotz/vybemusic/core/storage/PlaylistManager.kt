package com.alphabotz.vybemusic.core.storage

import android.content.Context
import android.content.SharedPreferences
import com.alphabotz.vybemusic.core.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Playlist(
    val id: String,
    val title: String,
    val description: String = "",
    val coverUrl: String = "",
    val tracks: List<Track> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

object PlaylistManager {
    private const val PREFS_NAME = "vybe_playlists_prefs"
    private const val KEY_LIKED_TRACKS = "liked_tracks_json"
    private const val KEY_USER_PLAYLISTS = "user_playlists_json"

    private val json = Json { ignoreUnknownKeys = true }
    private var prefs: SharedPreferences? = null

    private val _likedTracks = MutableStateFlow<List<Track>>(emptyList())
    val likedTracks: StateFlow<List<Track>> = _likedTracks.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadLikedTracks()
            loadPlaylists()
        }
    }

    private fun loadLikedTracks() {
        val raw = prefs?.getString(KEY_LIKED_TRACKS, null) ?: return
        try {
            val list = json.decodeFromString<List<Track>>(raw)
            _likedTracks.value = list
        } catch (e: Exception) {
            _likedTracks.value = emptyList()
        }
    }

    private fun saveLikedTracks() {
        try {
            val raw = json.encodeToString(_likedTracks.value)
            prefs?.edit()?.putString(KEY_LIKED_TRACKS, raw)?.apply()
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun loadPlaylists() {
        val raw = prefs?.getString(KEY_USER_PLAYLISTS, null)
        if (raw != null) {
            try {
                _playlists.value = json.decodeFromString<List<Playlist>>(raw)
            } catch (e: Exception) {
                _playlists.value = defaultPlaylists()
            }
        } else {
            _playlists.value = defaultPlaylists()
            savePlaylists()
        }
    }

    private fun savePlaylists() {
        try {
            val raw = json.encodeToString(_playlists.value)
            prefs?.edit()?.putString(KEY_USER_PLAYLISTS, raw)?.apply()
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun defaultPlaylists(): List<Playlist> {
        return listOf(
            Playlist(
                id = "pl_chill",
                title = "Late Night Vybes",
                description = "Deep chill, Lo-Fi beats and smooth rhythms",
                coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400"
            ),
            Playlist(
                id = "pl_hype",
                title = "Energy & Heavy Rotation",
                description = "High energy hip-hop, bass boosters and gym anthems",
                coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400"
            )
        )
    }

    fun isLiked(trackId: String): Boolean {
        return _likedTracks.value.any { it.id == trackId }
    }

    fun toggleLike(track: Track): Boolean {
        val current = _likedTracks.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == track.id }
        val nowLiked: Boolean
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
            nowLiked = false
        } else {
            current.add(0, track.copy(isLiked = true))
            nowLiked = true
        }
        _likedTracks.value = current
        saveLikedTracks()
        return nowLiked
    }

    fun createPlaylist(title: String, description: String = ""): Playlist {
        val newPl = Playlist(
            id = "pl_" + System.currentTimeMillis(),
            title = title.ifBlank { "My Playlist" },
            description = description,
            coverUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=400"
        )
        _playlists.value = listOf(newPl) + _playlists.value
        savePlaylists()
        return newPl
    }

    fun addTrackToPlaylist(playlistId: String, track: Track) {
        val updated = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                if (pl.tracks.none { it.id == track.id }) {
                    val cover = if (pl.coverUrl.isBlank() || pl.coverUrl.contains("unsplash")) {
                        track.artworkUrl
                    } else pl.coverUrl
                    pl.copy(tracks = pl.tracks + track, coverUrl = cover)
                } else pl
            } else pl
        }
        _playlists.value = updated
        savePlaylists()
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        val updated = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                pl.copy(tracks = pl.tracks.filterNot { it.id == trackId })
            } else pl
        }
        _playlists.value = updated
        savePlaylists()
    }

    fun deletePlaylist(playlistId: String) {
        _playlists.value = _playlists.value.filterNot { it.id == playlistId }
        savePlaylists()
    }
}
