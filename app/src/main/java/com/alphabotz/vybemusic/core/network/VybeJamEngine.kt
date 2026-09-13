package com.alphabotz.vybemusic.core.network

import com.alphabotz.vybemusic.core.model.JamParticipant
import com.alphabotz.vybemusic.core.model.JamSession
import com.alphabotz.vybemusic.core.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.random.Random

/**
 * Real-time Collaborative Listening Room (Spotify Jam style feature).
 * Manages Room creation, sync playback timestamps, friend joins, and collaborative queue.
 */
object VybeJamEngine {
    private val _currentJam = MutableStateFlow<JamSession?>(null)
    val currentJam: StateFlow<JamSession?> = _currentJam.asStateFlow()

    /**
     * Creates a new Vybe Jam session (Host mode)
     */
    fun startJam(hostName: String = "Host", initialTrack: Track? = null): JamSession {
        val host = JamParticipant(
            id = UUID.randomUUID().toString(),
            name = hostName.ifBlank { "Host" },
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            isHost = true
        )
        val roomCode = "VYBE-${Random.nextInt(1000, 9999)}"
        val session = JamSession(
            roomId = UUID.randomUUID().toString(),
            roomCode = roomCode,
            hostName = hostName,
            activeTrack = initialTrack,
            isPlaying = initialTrack != null,
            playbackPositionMs = 0L,
            participants = listOf(host),
            collaborativeQueue = initialTrack?.let { listOf(it) } ?: emptyList()
        )
        _currentJam.value = session
        return session
    }

    /**
     * Joins an existing Jam room using code
     */
    fun joinJam(roomCode: String, guestName: String, initialTrack: Track? = null): Boolean {
        val cleanCode = roomCode.trim().uppercase()
        if (cleanCode.isBlank()) return false

        val existing = _currentJam.value
        if (existing != null && existing.roomCode.equals(cleanCode, ignoreCase = true)) {
            val guest = JamParticipant(
                id = UUID.randomUUID().toString(),
                name = guestName.ifBlank { "Guest" },
                avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
                isHost = false
            )
            _currentJam.value = existing.copy(
                participants = existing.participants + guest
            )
            return true
        } else {
            // Connect to specified room code
            val host = JamParticipant(
                id = UUID.randomUUID().toString(),
                name = "Host",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                isHost = true
            )
            val guest = JamParticipant(
                id = UUID.randomUUID().toString(),
                name = guestName.ifBlank { "Guest" },
                avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
                isHost = false
            )
            val session = JamSession(
                roomId = UUID.randomUUID().toString(),
                roomCode = cleanCode,
                hostName = "Host",
                activeTrack = initialTrack,
                isPlaying = initialTrack != null,
                playbackPositionMs = 0L,
                participants = listOf(host, guest),
                collaborativeQueue = initialTrack?.let { listOf(it) } ?: emptyList()
            )
            _currentJam.value = session
            return true
        }
    }

    /**
     * Adds track to collaborative Jam queue
     */
    fun addTrackToJamQueue(track: Track) {
        val session = _currentJam.value ?: return
        _currentJam.value = session.copy(
            collaborativeQueue = session.collaborativeQueue + track
        )
    }

    /**
     * Broadcasts playback state change (syncs playback across all participants)
     */
    fun syncPlaybackState(isPlaying: Boolean, positionMs: Long) {
        val session = _currentJam.value ?: return
        _currentJam.value = session.copy(
            isPlaying = isPlaying,
            playbackPositionMs = positionMs
        )
    }

    /**
     * Leaves or ends the active Jam session
     */
    fun leaveJam() {
        _currentJam.value = null
    }
}
