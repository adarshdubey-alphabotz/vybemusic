package com.alphabotz.vybemusic.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alphabotz.vybemusic.core.network.VybeJamEngine
import com.alphabotz.vybemusic.core.playback.VybePlayerController
import com.alphabotz.vybemusic.ui.components.AppleMusicPlayer
import com.alphabotz.vybemusic.ui.components.VybeJamSheet
import com.alphabotz.vybemusic.ui.components.VybeMiniPlayer
import com.alphabotz.vybemusic.ui.theme.*

enum class NavTab { HOME, EXPLORE, SEARCH, JAM }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (playbackState.currentTrack != null) 140.dp else 85.dp)
        ) {
            when (currentTab) {
                NavTab.HOME -> HomeScreen(
                    onTrackSelect = { track, queue -> playerController.playTrack(track, queue) },
                    onOpenJam = { isJamSheetOpen = true },
                    onOpenSearch = { currentTab = NavTab.SEARCH }
                )
                NavTab.EXPLORE -> ExploreScreen()
                NavTab.SEARCH -> SearchScreen(
                    onTrackSelect = { track, queue -> playerController.playTrack(track, queue) }
                )
                NavTab.JAM -> {
                    LaunchedEffect(Unit) {
                        isJamSheetOpen = true
                        currentTab = NavTab.HOME
                    }
                }
            }
        }

        // Floating Bottom Controls & Glass Dock
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Floating Mini-Player (if song active)
            if (playbackState.currentTrack != null) {
                VybeMiniPlayer(
                    playbackState = playbackState,
                    onTogglePlayPause = { playerController.togglePlayPause() },
                    onNext = { playerController.playNext() },
                    onClick = { isPlayerExpanded = true }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Floating Liquid Frosted Glass Bottom Dock (Inspiration 1)
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color(0xEE11121A))
                    .border(1.dp, Color(0x38FFFFFF), RoundedCornerShape(36.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(0.92f)
                ) {
                    DockItem(
                        icon = Icons.Filled.Home,
                        isSelected = currentTab == NavTab.HOME,
                        onClick = { currentTab = NavTab.HOME }
                    )

                    DockItem(
                        icon = Icons.Outlined.Explore,
                        isSelected = currentTab == NavTab.EXPLORE,
                        onClick = { currentTab = NavTab.EXPLORE }
                    )

                    DockItem(
                        icon = Icons.Default.Search,
                        isSelected = currentTab == NavTab.SEARCH,
                        onClick = { currentTab = NavTab.SEARCH }
                    )

                    DockItem(
                        icon = Icons.Default.Group,
                        isSelected = isJamSheetOpen,
                        isJamActive = activeJam != null,
                        onClick = { isJamSheetOpen = true }
                    )
                }
            }
        }

        // Full Apple Music Player Screen Modal
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

        // Spotify Jam Modal Bottom Sheet
        if (isJamSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isJamSheetOpen = false },
                containerColor = Color(0xFF141520),
                contentColor = Color.White
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

@Composable
fun DockItem(
    icon: ImageVector,
    isSelected: Boolean,
    isJamActive: Boolean = false,
    onClick: () -> Unit
) {
    if (isSelected) {
        // Active Volt Lime Pill Badge
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(VybeVolt)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    } else {
        // Inactive Glass Tab
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isJamActive) VybeCyan else Color.White.copy(alpha = 0.65f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
