package com.alphabotz.vybemusic.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.playback.PlaybackState
import com.alphabotz.vybemusic.ui.theme.*

/**
 * Premium Apple Music inspired full-screen player.
 * Fluid ambient lighting, lossless audio badge, synchronized karaoke lyrics toggle,
 * and collaborative Vybe Jam controls.
 */
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
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return
    var showLyrics by remember { mutableStateOf(false) }

    DynamicMeshBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClosePlayer) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING FROM PLAYLIST",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VybeTextTertiary,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = if (track.album.isNotBlank()) track.album else "Vybe Hits",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onOpenJam) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Vybe Jam",
                        tint = VybePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Center View: Either Album Artwork or Karaoke Lyrics
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
                        // 3D Apple Music Album Card
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .shadow(
                                    elevation = 28.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    ambientColor = VybePrimary,
                                    spotColor = VybeAccent
                                )
                                .clip(RoundedCornerShape(24.dp))
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

            Spacer(modifier = Modifier.height(24.dp))

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
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = VybeTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Apple Music Style Lossless Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = VybeSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VybeSurfaceBorder),
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LOSSLESS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = VybeEmerald,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Slider
            val currentPos = playbackState.currentPositionMs.toFloat()
            val totalDur = playbackState.durationMs.coerceAtLeast(1L).toFloat()
            var sliderPos by remember(currentPos) { mutableFloatStateOf(currentPos) }

            Slider(
                value = sliderPos,
                onValueChange = { sliderPos = it },
                onValueChangeFinished = { onSeekTo(sliderPos.toLong()) },
                valueRange = 0f..totalDur,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
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
                    text = formatMs(sliderPos.toLong()),
                    fontSize = 12.sp,
                    color = VybeTextSecondary
                )
                Text(
                    text = "-" + formatMs((totalDur - sliderPos).toLong()),
                    fontSize = 12.sp,
                    color = VybeTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Controls Bar (Prev, Play/Pause, Next)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Toggle Shuffle */ }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playbackState.isShuffle) VybePrimary else VybeTextTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = onPrevious) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Apple Music Large Play Button
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(70.dp)
                ) {
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                IconButton(onClick = onNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                IconButton(onClick = { /* Toggle Repeat */ }) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (playbackState.isRepeat) VybePrimary else VybeTextTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Action Bar: Karaoke Lyrics Button & Vybe Jam indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showLyrics = !showLyrics }) {
                    Icon(
                        imageVector = if (showLyrics) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Karaoke Lyrics",
                        tint = if (showLyrics) VybePrimary else VybeTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Button(
                    onClick = onOpenJam,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlassOverlay
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Vybe Jam",
                        tint = VybeCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Vybe Jam",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(onClick = { /* Queue view */ }) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        tint = VybeTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
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
