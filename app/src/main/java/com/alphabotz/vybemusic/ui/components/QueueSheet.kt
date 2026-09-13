package com.alphabotz.vybemusic.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun QueueSheet(
    playbackState: PlaybackState,
    onJumpToTrack: (Int) -> Unit,
    onRemoveFromQueue: (Int) -> Unit,
    onClearQueue: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTrack = playbackState.currentTrack
    val queue = playbackState.queue
    val currentIndex = playbackState.queueIndex

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .background(Color(0xFF12131F), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(20.dp)
    ) {
        // Drag handle & Header
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.3f))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = "Queue",
                    tint = VybeVolt,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Playing Queue",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${queue.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA0A5BA)
                )
            }

            // Shuffle & Repeat Actions
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playbackState.isShuffle) VybeVolt else Color.White.copy(alpha = 0.6f)
                    )
                }

                IconButton(onClick = onToggleRepeat) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (playbackState.isRepeat) VybeVolt else Color.White.copy(alpha = 0.6f)
                    )
                }

                TextButton(onClick = onClearQueue) {
                    Text("Clear", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Now Playing Section
        if (currentTrack != null) {
            Text(
                text = "NOW PLAYING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = VybeVolt,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VybeVolt.copy(alpha = 0.12f))
                    .border(1.dp, VybeVolt.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                AsyncImage(
                    model = currentTrack.artworkUrl,
                    contentDescription = currentTrack.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentTrack.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentTrack.artist,
                        fontSize = 12.sp,
                        color = Color(0xFFA0A5BA),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Live animated equalizer wave pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VybeVolt)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "PLAYING",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Up Next List
        Text(
            text = "UP NEXT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFA0A5BA),
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val upNextItems = queue.filterIndexed { index, _ -> index != currentIndex }

        if (upNextItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No more tracks in queue. Search or add tracks to play next!",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(queue) { index, track ->
                    if (index != currentIndex) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x18FFFFFF))
                                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                                .clickable { onJumpToTrack(index) }
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "%02d".format(index + 1),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.width(26.dp)
                            )

                            AsyncImage(
                                model = track.artworkUrl,
                                contentDescription = track.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = track.artist,
                                    fontSize = 12.sp,
                                    color = Color(0xFFA0A5BA),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Remove from queue button
                            IconButton(
                                onClick = { onRemoveFromQueue(index) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
