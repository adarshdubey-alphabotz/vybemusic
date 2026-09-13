package com.alphabotz.vybemusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.alphabotz.vybemusic.core.playback.VybePlayerController
import com.alphabotz.vybemusic.ui.screens.MainScaffold
import com.alphabotz.vybemusic.ui.theme.VybeBackground
import com.alphabotz.vybemusic.ui.theme.VybeMusicTheme

class MainActivity : ComponentActivity() {
    private lateinit var playerController: VybePlayerController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playerController = VybePlayerController.getInstance(applicationContext)
        com.alphabotz.vybemusic.core.storage.UserProfileManager.init(applicationContext)
        com.alphabotz.vybemusic.core.storage.PlaylistManager.init(applicationContext)

        // Spotify/Apple Music standard: back button minimises app without terminating audio
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        })

        setContent {
            VybeMusicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VybeBackground
                ) {
                    MainScaffold(playerController = playerController)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Do NOT release playerController here so background playback continues uninterrupted!
    }
}
