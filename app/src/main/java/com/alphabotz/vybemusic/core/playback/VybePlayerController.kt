package com.alphabotz.vybemusic.core.playback

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import com.alphabotz.vybemusic.core.model.Lyrics
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.network.LrclibLyricsApi
import com.alphabotz.vybemusic.core.network.VybeJamEngine
import com.alphabotz.vybemusic.core.network.VybeMusicEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class PlaybackState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isBuffering: Boolean = false,
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = false,
    val queue: List<Track> = emptyList(),
    val queueIndex: Int = 0,
    val activeLyrics: Lyrics? = null,
    val activeLyricLineIndex: Int = -1,
    val errorMessage: String? = null
)

class VybePlayerController(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    init {
        initializePlayer()
        startPositionTracker()
    }

    private fun initializePlayer() {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(20000)

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)

        val player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
                VybeJamEngine.syncPlaybackState(isPlaying, player.currentPosition)
            }

            override fun onPlaybackStateChanged(state: Int) {
                val isBuffering = state == Player.STATE_BUFFERING
                _playbackState.value = _playbackState.value.copy(
                    isBuffering = isBuffering,
                    durationMs = player.duration.coerceAtLeast(0L)
                )
                if (state == Player.STATE_ENDED) {
                    handleTrackEnded()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("VybePlayerController", "Playback error: ${error.errorCodeName}", error)
                _playbackState.value = _playbackState.value.copy(
                    errorMessage = error.localizedMessage,
                    isPlaying = false
                )
            }
        })

        exoPlayer = player

        try {
            mediaSession = MediaSession.Builder(context, player).build()
        } catch (e: Exception) {
            Log.e("VybePlayerController", "MediaSession init error", e)
        }
    }

    fun playTrack(track: Track, newQueue: List<Track> = listOf(track)) {
        val index = newQueue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
        _playbackState.value = _playbackState.value.copy(
            currentTrack = track,
            queue = newQueue,
            queueIndex = index,
            activeLyrics = null,
            activeLyricLineIndex = -1,
            errorMessage = null
        )

        // Fetch Synced Lyrics in Background
        scope.launch {
            val lyrics = LrclibLyricsApi.getLyrics(track.id, track.title, track.artist)
            if (_playbackState.value.currentTrack?.id == track.id) {
                _playbackState.value = _playbackState.value.copy(activeLyrics = lyrics)
            }
        }

        // Start Streaming with ExoPlayer & Rich MediaMetadata for System Popup/Lock Screen
        exoPlayer?.apply {
            stop()
            clearMediaItems()

            val metadata = MediaMetadata.Builder()
                .setTitle(track.title)
                .setArtist(track.artist)
                .setAlbumTitle(track.album)
                .setArtworkUri(Uri.parse(track.artworkUrl))
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(track.streamUrl)
                .setMediaMetadata(metadata)
                .build()

            setMediaItem(mediaItem)
            prepare()
            play()
        }

        // Contextual Smart Auto-Queue: If queue has few tracks, load similar genre tracks
        if (newQueue.size <= 3) {
            scope.launch {
                val suggestions = VybeMusicEngine.getTrendingTracks(track.language)
                val cleanSuggestions = suggestions.filter { s -> newQueue.none { it.id == s.id } }
                if (cleanSuggestions.isNotEmpty()) {
                    _playbackState.value = _playbackState.value.copy(
                        queue = _playbackState.value.queue + cleanSuggestions
                    )
                }
            }
        }
    }

    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                if (it.playbackState == Player.STATE_ENDED) {
                    it.seekTo(0)
                }
                it.play()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
        updateActiveLyricLine(positionMs)
        VybeJamEngine.syncPlaybackState(_playbackState.value.isPlaying, positionMs)
    }

    fun playNext() {
        val state = _playbackState.value
        if (state.queue.isNotEmpty()) {
            val nextIndex = if (state.isShuffle) {
                Random.nextInt(state.queue.size)
            } else {
                (state.queueIndex + 1) % state.queue.size
            }
            playTrack(state.queue[nextIndex], state.queue)
        }
    }

    fun playPrevious() {
        val state = _playbackState.value
        if (state.queue.isNotEmpty()) {
            val prevIndex = if (state.queueIndex - 1 < 0) state.queue.size - 1 else state.queueIndex - 1
            playTrack(state.queue[prevIndex], state.queue)
        }
    }

    fun jumpToQueueItem(index: Int) {
        val state = _playbackState.value
        if (index in state.queue.indices) {
            playTrack(state.queue[index], state.queue)
        }
    }

    fun addToQueue(track: Track) {
        val currentQueue = _playbackState.value.queue
        if (currentQueue.isEmpty()) {
            playTrack(track, listOf(track))
        } else {
            _playbackState.value = _playbackState.value.copy(queue = currentQueue + track)
        }
    }

    fun playNextInQueue(track: Track) {
        val state = _playbackState.value
        val list = state.queue.toMutableList()
        val insertIndex = (state.queueIndex + 1).coerceAtMost(list.size)
        list.add(insertIndex, track)
        _playbackState.value = state.copy(queue = list)
    }

    fun removeFromQueue(index: Int) {
        val state = _playbackState.value
        if (index in state.queue.indices && state.queue.size > 1) {
            val list = state.queue.toMutableList()
            list.removeAt(index)
            val newCurrentIndex = if (index < state.queueIndex) state.queueIndex - 1 else state.queueIndex
            _playbackState.value = state.copy(queue = list, queueIndex = newCurrentIndex)
        }
    }

    fun clearQueue() {
        val state = _playbackState.value
        val current = state.currentTrack
        if (current != null) {
            _playbackState.value = state.copy(queue = listOf(current), queueIndex = 0)
        } else {
            _playbackState.value = state.copy(queue = emptyList(), queueIndex = 0)
        }
    }

    fun toggleShuffle() {
        _playbackState.value = _playbackState.value.copy(isShuffle = !_playbackState.value.isShuffle)
    }

    fun toggleRepeat() {
        _playbackState.value = _playbackState.value.copy(isRepeat = !_playbackState.value.isRepeat)
    }

    private fun handleTrackEnded() {
        val state = _playbackState.value
        if (state.isRepeat) {
            seekTo(0)
            exoPlayer?.play()
        } else {
            playNext()
        }
    }

    private fun startPositionTracker() {
        scope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        val pos = player.currentPosition
                        val dur = player.duration.coerceAtLeast(0L)
                        _playbackState.value = _playbackState.value.copy(
                            currentPositionMs = pos,
                            durationMs = dur
                        )
                        updateActiveLyricLine(pos)
                    }
                }
                delay(250)
            }
        }
    }

    private fun updateActiveLyricLine(positionMs: Long) {
        val lyrics = _playbackState.value.activeLyrics ?: return
        val activeIndex = lyrics.getActiveLineIndex(positionMs)
        if (activeIndex != _playbackState.value.activeLyricLineIndex) {
            _playbackState.value = _playbackState.value.copy(activeLyricLineIndex = activeIndex)
        }
    }

    fun release() {
        mediaSession?.release()
        mediaSession = null
        exoPlayer?.release()
        exoPlayer = null
        scope.cancel()
    }
}
