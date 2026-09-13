package com.alphabotz.vybemusic.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.playback.PlaybackState
import com.alphabotz.vybemusic.core.playback.SleepTimerManager
import com.alphabotz.vybemusic.core.storage.PlaylistManager
import androidx.compose.ui.platform.LocalContext
import com.alphabotz.vybemusic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppleMusicPlayer(
    playbackState: PlaybackState,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onOpenJam: () -> Unit,
    onClosePlayer: () -> Unit,
    onJumpToTrack: (Int) -> Unit = {},
    onRemoveFromQueue: (Int) -> Unit = {},
    onClearQueue: () -> Unit = {},
    onToggleShuffle: () -> Unit = {},
    onToggleRepeat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return
    var showLyrics by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var showSleepPicker by remember { mutableStateOf(false) }
    var isVinylMode by remember { mutableStateOf(false) }

    // Precise scrubbing state so position updates never jump while user drags
    var isScrubbing by remember { mutableStateOf(false) }
    var scrubPosition by remember { mutableFloatStateOf(0f) }

    val totalDur = playbackState.durationMs.coerceAtLeast(1L).toFloat()
    val sliderValue = if (isScrubbing) scrubPosition else playbackState.currentPositionMs.toFloat()

    DynamicMeshBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dismiss Pill Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x2BFFFFFF))
                        .clickable { onClosePlayer() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Playlist / Album Label
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING FROM VYBE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VybeTextTertiary,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = if (track.album.isNotBlank()) track.album else "Vybe Hits",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Jam Room Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x2BFFFFFF))
                        .clickable { onOpenJam() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Vybe Jam",
                        tint = VybeVolt,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Center View: 3D CoverFlow Carousel OR Karaoke Synced Lyrics
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = showLyrics,
                    label = "ArtworkOrLyrics"
                ) { lyricsVisible ->
                    if (lyricsVisible) {
                        KaraokeLyricsView(
                            lyrics = playbackState.activeLyrics,
                            activeLineIndex = playbackState.activeLyricLineIndex,
                            onSeekToLine = onSeekTo
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CoverFlowCarousel(
                                playbackState = playbackState,
                                onPrevious = onPrevious,
                                onNext = onNext,
                                isVinylMode = isVinylMode
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Mode Toggle: Artwork vs Vinyl 33 RPM
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (!isVinylMode) Color(0x33FFFFFF) else Color(0x11FFFFFF),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (!isVinylMode) VybeVolt else Color(0x22FFFFFF)),
                                    modifier = Modifier.clickable { isVinylMode = false }
                                ) {
                                    Text(
                                        text = "🖼️ Artwork",
                                        color = if (!isVinylMode) VybeVolt else VybeTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isVinylMode) Color(0x33FFFFFF) else Color(0x11FFFFFF),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isVinylMode) VybeVolt else Color(0x22FFFFFF)),
                                    modifier = Modifier.clickable { isVinylMode = true }
                                ) {
                                    Text(
                                        text = "💿 Vinyl Spin",
                                        color = if (isVinylMode) VybeVolt else VybeTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Track Meta & Lossless Audio Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFA5AABF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Quick Like Button
                val likedTracks by PlaylistManager.likedTracks.collectAsState()
                val isLiked = remember(likedTracks, track.id) { PlaylistManager.isLiked(track.id) }
                IconButton(onClick = { PlaylistManager.toggleLike(track) }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like Song",
                        tint = if (isLiked) Color(0xFFFF3366) else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Sleep Timer Pill
                val remainingSleep by SleepTimerManager.remainingSeconds.collectAsState()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (remainingSleep != null) VybeVolt.copy(alpha = 0.2f) else Color(0x26FFFFFF))
                        .border(1.dp, if (remainingSleep != null) VybeVolt else Color(0x35FFFFFF), RoundedCornerShape(8.dp))
                        .clickable { showSleepPicker = true }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Bedtime,
                            contentDescription = "Sleep Timer",
                            tint = if (remainingSleep != null) VybeVolt else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        if (remainingSleep != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${remainingSleep!! / 60}m",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VybeVolt
                            )
                        }
                    }
                }

                // Apple Music Style Lossless Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x26FFFFFF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x35FFFFFF)),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LOSSLESS 320K",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = VybeVolt,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Time tooltip while scrubbing
            if (isScrubbing) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xEE12131F),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VybeVolt)
                ) {
                    Text(
                        text = "${formatMs(scrubPosition.toLong())} / ${formatMs(totalDur.toLong())}",
                        color = VybeVolt,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Scrubbable Progress Slider with zero-lag user tracking
            Slider(
                value = sliderValue.coerceIn(0f, totalDur),
                onValueChange = {
                    isScrubbing = true
                    scrubPosition = it
                },
                onValueChangeFinished = {
                    onSeekTo(scrubPosition.toLong())
                    isScrubbing = false
                },
                valueRange = 0f..totalDur,
                colors = SliderDefaults.colors(
                    thumbColor = VybeVolt,
                    activeTrackColor = VybeVolt,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Timestamps (Elapsed / Remaining)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(sliderValue.toLong()),
                    fontSize = 12.sp,
                    color = VybeTextSecondary
                )
                Text(
                    text = "-" + formatMs((totalDur - sliderValue).toLong().coerceAtLeast(0L)),
                    fontSize = 12.sp,
                    color = VybeTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Floating Frosted Glass Liquid Player Dock (Inspiration 2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xDD181926))
                    .border(1.dp, Color(0x38FFFFFF), RoundedCornerShape(32.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Controls: Prev, Play/Pause, Next
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = onPrevious) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        // Play/Pause Circular Pill
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(VybeVolt)
                                .clickable { onTogglePlayPause() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        IconButton(onClick = onNext) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // Right Controls: Lyrics, Jam, Queue
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Lyrics Toggle Button
                        IconButton(onClick = { showLyrics = !showLyrics }) {
                            Icon(
                                imageVector = if (showLyrics) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Karaoke Lyrics",
                                tint = if (showLyrics) VybeVolt else Color.White.copy(alpha = 0.75f),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Jam Room Button
                        IconButton(onClick = onOpenJam) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Vybe Jam",
                                tint = VybeCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Up Next Queue Sheet Button
                        IconButton(onClick = { showQueueSheet = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                contentDescription = "Queue",
                                tint = if (showQueueSheet) VybeVolt else Color.White.copy(alpha = 0.75f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Up Next Queue Modal Sheet
        if (showQueueSheet) {
            ModalBottomSheet(
                onDismissRequest = { showQueueSheet = false },
                containerColor = Color(0xFF12131F),
                contentColor = Color.White
            ) {
                QueueSheet(
                    playbackState = playbackState,
                    onJumpToTrack = {
                        onJumpToTrack(it)
                        showQueueSheet = false
                    },
                    onRemoveFromQueue = onRemoveFromQueue,
                    onClearQueue = onClearQueue,
                    onToggleShuffle = onToggleShuffle,
                    onToggleRepeat = onToggleRepeat,
                    onClose = { showQueueSheet = false }
                )
            }
        }

        // Sleep Timer Bottom Sheet
        if (showSleepPicker) {
            val context = LocalContext.current
            val remainingSleep by SleepTimerManager.remainingSeconds.collectAsState()

            ModalBottomSheet(
                onDismissRequest = { showSleepPicker = false },
                containerColor = Color(0xFF141522),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Sleep Timer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val presets = listOf(15, 30, 45, 60)
                    presets.forEach { minutes ->
                        ActionSheetRow(
                            icon = Icons.Outlined.Timer,
                            title = "$minutes Minutes",
                            subtitle = "Pauses playback automatically in $minutes min"
                        ) {
                            SleepTimerManager.startTimer(context, minutes)
                            showSleepPicker = false
                        }
                    }

                    ActionSheetRow(
                        icon = Icons.Outlined.MusicOff,
                        title = "End of This Song",
                        subtitle = "Stops when current song finishes"
                    ) {
                        SleepTimerManager.setStopAtEndOfSong(context, true)
                        showSleepPicker = false
                    }

                    if (remainingSleep != null) {
                        ActionSheetRow(
                            icon = Icons.Outlined.Cancel,
                            title = "Turn Off Timer",
                            subtitle = "Cancel active sleep countdown",
                            iconTint = Color.Red
                        ) {
                            SleepTimerManager.cancelTimer()
                            showSleepPicker = false
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

/**
 * 3D Perspective CoverFlow Carousel (Inspiration 2)
 */
@Composable
fun CoverFlowCarousel(
    playbackState: PlaybackState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isVinylMode: Boolean = false
) {
    val track = playbackState.currentTrack ?: return
    val queue = playbackState.queue
    val currentIndex = playbackState.queueIndex

    val prevTrack = if (queue.isNotEmpty() && currentIndex > 0) queue[currentIndex - 1] else null
    val nextTrack = if (queue.isNotEmpty() && currentIndex < queue.size - 1) queue[currentIndex + 1] else null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp),
        contentAlignment = Alignment.Center
    ) {
        // Left Flank Card (Previous Song)
        if (prevTrack != null && !isVinylMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 10.dp)
                    .graphicsLayer {
                        rotationY = 32f
                        scaleX = 0.78f
                        scaleY = 0.78f
                        alpha = 0.55f
                        cameraDistance = 12f * density
                    }
                    .size(220.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(22.dp))
                    .clickable { onPrevious() }
            ) {
                AsyncImage(
                    model = prevTrack.artworkUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400" },
                    contentDescription = prevTrack.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Right Flank Card (Next Song)
        if (nextTrack != null && !isVinylMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-10).dp)
                    .graphicsLayer {
                        rotationY = -32f
                        scaleX = 0.78f
                        scaleY = 0.78f
                        alpha = 0.55f
                        cameraDistance = 12f * density
                    }
                    .size(220.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(22.dp))
                    .clickable { onNext() }
            ) {
                AsyncImage(
                    model = nextTrack.artworkUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400" },
                    contentDescription = nextTrack.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Center Hero Card (Current Song)
        if (isVinylMode) {
            val infiniteTransition = rememberInfiniteTransition(label = "VinylSpin")
            val angle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 6000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "VinylAngle"
            )
            val currentAngle = if (playbackState.isPlaying) angle else 0f

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(265.dp)
                    .rotate(currentAngle)
                    .shadow(32.dp, CircleShape, ambientColor = VybeVolt.copy(alpha = 0.4f), spotColor = Color.Black)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF22222A),
                                Color(0xFF0F0F14),
                                Color(0xFF1E1E26),
                                Color(0xFF0A0A0E),
                                Color(0xFF16161E),
                                Color(0xFF060608)
                            )
                        )
                    )
                    .border(3.dp, Color(0x33FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Vinyl Grooves
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .border(1.dp, Color(0x22FFFFFF), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .border(1.dp, Color(0x18FFFFFF), CircleShape)
                )
                // Center Album Label
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .border(2.dp, VybeVolt, CircleShape)
                ) {
                    AsyncImage(
                        model = track.artworkUrl,
                        contentDescription = track.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Center Spindle Hole
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF060608))
                        .border(1.5.dp, Color(0x66FFFFFF), CircleShape)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(260.dp)
                    .shadow(
                        elevation = 32.dp,
                        shape = RoundedCornerShape(26.dp),
                        ambientColor = VybeVolt.copy(alpha = 0.35f),
                        spotColor = VybeAccent.copy(alpha = 0.5f)
                    )
                    .clip(RoundedCornerShape(26.dp))
                    .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(26.dp))
            ) {
                AsyncImage(
                    model = track.artworkUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500" },
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSecs = (ms / 1000).coerceAtLeast(0)
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return "%d:%02d".format(mins, secs)
}
