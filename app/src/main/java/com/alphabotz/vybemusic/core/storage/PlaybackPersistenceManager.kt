package com.alphabotz.vybemusic.core.storage

import android.content.Context
import android.content.SharedPreferences
import com.alphabotz.vybemusic.core.model.Track

object PlaybackPersistenceManager {
    private const val PREFS_NAME = "vybe_playback_prefs"
    private const val KEY_TRACK_ID = "last_track_id"
    private const val KEY_TRACK_TITLE = "last_track_title"
    private const val KEY_TRACK_ARTIST = "last_track_artist"
    private const val KEY_TRACK_ALBUM = "last_track_album"
    private const val KEY_TRACK_DURATION = "last_track_duration"
    private const val KEY_TRACK_ART = "last_track_art"
    private const val KEY_TRACK_STREAM = "last_track_stream"
    private const val KEY_TRACK_QUALITY = "last_track_quality"
    private const val KEY_TRACK_LANG = "last_track_lang"
    private const val KEY_POSITION_MS = "last_position_ms"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveLastPlayback(track: Track, positionMs: Long) {
        prefs?.edit()?.apply {
            putString(KEY_TRACK_ID, track.id)
            putString(KEY_TRACK_TITLE, track.title)
            putString(KEY_TRACK_ARTIST, track.artist)
            putString(KEY_TRACK_ALBUM, track.album)
            putLong(KEY_TRACK_DURATION, track.durationSeconds)
            putString(KEY_TRACK_ART, track.artworkUrl)
            putString(KEY_TRACK_STREAM, track.streamUrl)
            putString(KEY_TRACK_QUALITY, track.audioQuality)
            putString(KEY_TRACK_LANG, track.language)
            putLong(KEY_POSITION_MS, positionMs)
            apply()
        }
    }

    fun getSavedPlayback(): Pair<Track?, Long> {
        val p = prefs ?: return Pair(null, 0L)
        val id = p.getString(KEY_TRACK_ID, null) ?: return Pair(null, 0L)
        val title = p.getString(KEY_TRACK_TITLE, null) ?: return Pair(null, 0L)
        val stream = p.getString(KEY_TRACK_STREAM, null) ?: return Pair(null, 0L)
        val artist = p.getString(KEY_TRACK_ARTIST, "Various Artists") ?: "Various Artists"
        val album = p.getString(KEY_TRACK_ALBUM, "Vybe Hits") ?: "Vybe Hits"
        val duration = p.getLong(KEY_TRACK_DURATION, 200L)
        val art = p.getString(KEY_TRACK_ART, "") ?: ""
        val quality = p.getString(KEY_TRACK_QUALITY, "320kbps Lossless AAC") ?: "320kbps Lossless AAC"
        val lang = p.getString(KEY_TRACK_LANG, "English") ?: "English"
        val pos = p.getLong(KEY_POSITION_MS, 0L)

        val track = Track(
            id = id,
            title = title,
            artist = artist,
            album = album,
            durationSeconds = duration,
            artworkUrl = art,
            streamUrl = stream,
            audioQuality = quality,
            language = lang
        )
        return Pair(track, pos)
    }
}
