package com.alphabotz.vybemusic.core.playback

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SleepTimerManager {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    private val _remainingSeconds = MutableStateFlow<Int?>(null)
    val remainingSeconds: StateFlow<Int?> = _remainingSeconds.asStateFlow()

    private val _stopAtEndOfSong = MutableStateFlow(false)
    val stopAtEndOfSong: StateFlow<Boolean> = _stopAtEndOfSong.asStateFlow()

    fun startTimer(context: Context, minutes: Int) {
        timerJob?.cancel()
        _stopAtEndOfSong.value = false
        val totalSeconds = minutes * 60
        _remainingSeconds.value = totalSeconds

        timerJob = scope.launch {
            var current = totalSeconds
            while (current > 0 && isActive) {
                delay(1000)
                current--
                _remainingSeconds.value = current
            }
            if (isActive && current <= 0) {
                _remainingSeconds.value = null
                VybePlayerController.getInstance(context).pause()
            }
        }
    }

    fun setStopAtEndOfSong(context: Context, enabled: Boolean) {
        if (enabled) {
            timerJob?.cancel()
            _remainingSeconds.value = null
            _stopAtEndOfSong.value = true
        } else {
            _stopAtEndOfSong.value = false
        }
    }

    fun onTrackEnded(context: Context): Boolean {
        if (_stopAtEndOfSong.value) {
            _stopAtEndOfSong.value = false
            _remainingSeconds.value = null
            VybePlayerController.getInstance(context).pause()
            return true
        }
        return false
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _remainingSeconds.value = null
        _stopAtEndOfSong.value = false
    }

    fun getFormattedRemaining(): String? {
        val sec = _remainingSeconds.value ?: return null
        val m = sec / 60
        val s = sec % 60
        return "%02d:%02d".format(m, s)
    }
}
