package com.alphabotz.vybemusic.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LyricsLine(
    val startTimeMs: Long,
    val text: String
)

@Serializable
data class Lyrics(
    val trackId: String,
    val isSynced: Boolean,
    val plainLyrics: String = "",
    val lines: List<LyricsLine> = emptyList()
) {
    /**
     * Finds the index of the line currently active at [playbackPositionMs]
     */
    fun getActiveLineIndex(playbackPositionMs: Long): Int {
        if (lines.isEmpty()) return -1
        for (i in lines.indices.reversed()) {
            if (playbackPositionMs >= lines[i].startTimeMs) {
                return i
            }
        }
        return 0
    }
}
