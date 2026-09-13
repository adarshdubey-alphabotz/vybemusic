package com.alphabotz.vybemusic.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationSeconds: Long = 0,
    val artworkUrl: String = "",
    val streamUrl: String = "",
    val audioQuality: String = "320kbps AAC", // e.g. "320kbps AAC", "Lossless Hi-Res"
    val language: String = "English",
    val hasLyrics: Boolean = true,
    val lyricsId: String? = null,
    val isLiked: Boolean = false
) {
    val formattedDuration: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
