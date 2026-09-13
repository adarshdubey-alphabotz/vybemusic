package com.alphabotz.vybemusic.core.playback

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.alphabotz.vybemusic.core.model.Lyrics
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.network.LrclibLyricsApi
import com.alphabotz.vybemusic.core.network.VybeJamEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

        exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
                        VybeJamEngine.syncPlaybackState(isPlaying, currentPosition)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        val isBuffering = playbackState == Player.STATE_BUFFERING
                        _playbackState.value = _playbackState.value.copy(
                            isBuffering = isBuffering,
                            durationMs = duration.coerceAtLeast(0L)
                        )
                        if (playbackState == Player.STATE_ENDED) {
                            playNext()
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

        // Start Streaming with ExoPlayer
        exoPlayer?.apply {
            stop()
            clearMediaItems()
            val mediaItem = MediaItem.fromUri(track.streamUrl)
            setMediaItem(mediaItem)
            prepare()
            play()
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
            val nextIndex = (state.queueIndex + 1) % state.queue.size
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
                delay(200)
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
        exoPlayer?.release()
        exoPlayer = null
        scope.cancel()
    }
}
