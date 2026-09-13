package com.alphabotz.vybemusic.core.model

import kotlinx.serialization.Serializable

@Serializable
data class JamParticipant(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isHost: Boolean = false,
    val joinedAtMs: Long = System.currentTimeMillis()
)

@Serializable
data class JamTrackVote(
    val trackId: String,
    val addedBy: JamParticipant,
    val upvotes: Int = 1
)

@Serializable
data class JamSession(
    val roomId: String,
    val roomCode: String, // 6-char code e.g. "VYBE-7729"
    val hostName: String,
    val activeTrack: Track?,
    val isPlaying: Boolean = false,
    val playbackPositionMs: Long = 0L,
    val participants: List<JamParticipant> = emptyList(),
    val collaborativeQueue: List<Track> = emptyList(),
    val createdAtMs: Long = System.currentTimeMillis()
)
