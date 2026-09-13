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

        playerController = VybePlayerController(applicationContext)
        com.alphabotz.vybemusic.core.storage.UserProfileManager.init(applicationContext)

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
        playerController.release()
    }
}
