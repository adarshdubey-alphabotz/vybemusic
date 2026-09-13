package com.alphabotz.vybemusic.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alphabotz.vybemusic.core.network.VybeJamEngine
import com.alphabotz.vybemusic.core.playback.VybePlayerController
import com.alphabotz.vybemusic.ui.components.AppleMusicPlayer
import com.alphabotz.vybemusic.ui.components.VybeJamSheet
import com.alphabotz.vybemusic.ui.components.VybeMiniPlayer
import com.alphabotz.vybemusic.ui.theme.*

enum class NavTab { HOME, EXPLORE, SEARCH, LIBRARY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    playerController: VybePlayerController
) {
    val playbackState by playerController.playbackState.collectAsState()
    val activeJam by VybeJamEngine.currentJam.collectAsState()

    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var isPlayerExpanded by remember { mutableStateOf(false) }
    var isJamSheetOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(VybeBackground)) {
        // Main Screen Content
        Scaffold(
            bottomBar = {
                Column {
                    // Floating YouTube Music Style Mini-Player
                    if (playbackState.currentTrack != null) {
                        VybeMiniPlayer(
                            playbackState = playbackState,
                            onTogglePlayPause = { playerController.togglePlayPause() },
                            onNext = { playerController.playNext() },
                            onClick = { isPlayerExpanded = true }
                        )
                    }

                    // Bottom Navigation Bar
                    NavigationBar(
                        containerColor = VybeSurface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == NavTab.HOME,
                            onClick = { currentTab = NavTab.HOME },
                            icon = { Icon(if (currentTab == NavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VybePrimary,
                                selectedTextColor = VybePrimary,
                                unselectedIconColor = VybeTextSecondary,
                                unselectedTextColor = VybeTextSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            selected = currentTab == NavTab.EXPLORE,
                            onClick = { currentTab = NavTab.EXPLORE },
                            icon = { Icon(if (currentTab == NavTab.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore, contentDescription = "Explore") },
                            label = { Text("Explore", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VybePrimary,
                                selectedTextColor = VybePrimary,
                                unselectedIconColor = VybeTextSecondary,
                                unselectedTextColor = VybeTextSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            selected = currentTab == NavTab.SEARCH,
                            onClick = { currentTab = NavTab.SEARCH },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            label = { Text("Search", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VybePrimary,
                                selectedTextColor = VybePrimary,
                                unselectedIconColor = VybeTextSecondary,
                                unselectedTextColor = VybeTextSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentTab) {
                    NavTab.HOME -> HomeScreen(
                        onTrackSelect = { track, queue -> playerController.playTrack(track, queue) },
                        onOpenJam = { isJamSheetOpen = true }
                    )
                    NavTab.EXPLORE -> ExploreScreen()
                    NavTab.SEARCH -> SearchScreen(
                        onTrackSelect = { track, queue -> playerController.playTrack(track, queue) }
                    )
                    NavTab.LIBRARY -> {}
                }
            }
        }

        // Animated Full Apple Music Player Modal
        AnimatedVisibility(
            visible = isPlayerExpanded && playbackState.currentTrack != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            AppleMusicPlayer(
                playbackState = playbackState,
                onTogglePlayPause = { playerController.togglePlayPause() },
                onSeekTo = { playerController.seekTo(it) },
                onNext = { playerController.playNext() },
                onPrevious = { playerController.playPrevious() },
                onOpenJam = { isJamSheetOpen = true },
                onClosePlayer = { isPlayerExpanded = false }
            )
        }

        // Spotify Jam Modal Sheet
        if (isJamSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isJamSheetOpen = false },
                containerColor = VybeSurface
            ) {
                VybeJamSheet(
                    activeJam = activeJam,
                    currentTrack = playbackState.currentTrack,
                    onStartJam = { host ->
                        VybeJamEngine.startJam(host, playbackState.currentTrack)
                    },
                    onJoinJam = { code, name ->
                        VybeJamEngine.joinJam(code, name)
                    },
                    onLeaveJam = {
                        VybeJamEngine.leaveJam()
                    }
                )
            }
        }
    }
}
