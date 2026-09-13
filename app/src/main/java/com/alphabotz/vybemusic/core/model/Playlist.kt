package com.alphabotz.vybemusic.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val coverUrl: String = "",
    val trackCount: Int = 0,
    val tracks: List<Track> = emptyList()
)
