package com.alphabotz.vybemusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.playback.SleepTimerManager
import com.alphabotz.vybemusic.core.storage.PlaylistManager
import com.alphabotz.vybemusic.ui.theme.VybeVolt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackActionSheet(
    track: Track,
    onDismiss: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onStartJam: () -> Unit
) {
    val context = LocalContext.current
    val likedTracks by PlaylistManager.likedTracks.collectAsState()
    val playlists by PlaylistManager.playlists.collectAsState()
    val remainingSleep by SleepTimerManager.remainingSeconds.collectAsState()

    val isLiked = remember(likedTracks, track) {
        PlaylistManager.isLiked(track.id)
    }

    var showPlaylistPicker by remember { mutableStateOf(false) }
    var showSleepTimerPicker by remember { mutableStateOf(false) }
    var showNewPlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141522),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Track Header Card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                AsyncImage(
                    model = track.artworkUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${track.artist} • ${track.audioQuality}",
                        fontSize = 12.sp,
                        color = Color(0xFFA0A5BA),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = Color(0x1FFFFFFF), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            if (!showPlaylistPicker && !showSleepTimerPicker) {
                // Action: Play Next
                ActionSheetRow(
                    icon = Icons.Filled.PlaylistPlay,
                    title = "Play Next",
                    subtitle = "Insert directly after current playing song"
                ) {
                    onPlayNext()
                    onDismiss()
                }

                // Action: Add to Queue
                ActionSheetRow(
                    icon = Icons.Outlined.QueueMusic,
                    title = "Add to Queue",
                    subtitle = "Append to the end of upcoming tracks"
                ) {
                    onAddToQueue()
                    onDismiss()
                }

                // Action: Like / Unlike Song
                ActionSheetRow(
                    icon = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    iconTint = if (isLiked) Color(0xFFFF3366) else Color.White,
                    title = if (isLiked) "Liked Song" else "Like this Song",
                    subtitle = if (isLiked) "Saved in your Liked Songs library" else "Add to your personal favorites"
                ) {
                    PlaylistManager.toggleLike(track)
                }

                // Action: Add to Playlist
                ActionSheetRow(
                    icon = Icons.Outlined.LibraryMusic,
                    title = "Add to Playlist...",
                    subtitle = "Organize into custom mixes and collections"
                ) {
                    showPlaylistPicker = true
                }

                // Action: Sleep Timer
                val sleepSub = remainingSleep?.let { s -> "Active: ${s / 60}m ${s % 60}s left" } ?: "Set timer to pause playback automatically"
                ActionSheetRow(
                    icon = Icons.Outlined.Bedtime,
                    title = "Sleep Timer",
                    subtitle = sleepSub,
                    badge = if (remainingSleep != null) "ON" else null
                ) {
                    showSleepTimerPicker = true
                }

                // Action: Start Vybe Jam
                ActionSheetRow(
                    icon = Icons.Outlined.Group,
                    title = "Start Vybe Jam",
                    subtitle = "Listen synchronously with friends anywhere"
                ) {
                    onStartJam()
                    onDismiss()
                }
            } else if (showPlaylistPicker) {
                // Playlist Selection Sub-view
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    IconButton(onClick = { showPlaylistPicker = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Add to Playlist", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { showNewPlaylistDialog = true }) {
                        Text("+ New", color = VybeVolt, fontWeight = FontWeight.Bold)
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp)) {
                    items(playlists) { pl ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    PlaylistManager.addTrackToPlaylist(pl.id, track)
                                    onDismiss()
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x33FFFFFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = VybeVolt)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pl.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${pl.tracks.size} tracks", color = Color(0xFFA0A5BA), fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else if (showSleepTimerPicker) {
                // Sleep Timer Sub-view
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    IconButton(onClick = { showSleepTimerPicker = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Set Sleep Timer", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                val options = listOf(15, 30, 45, 60)
                options.forEach { minutes ->
                    ActionSheetRow(
                        icon = Icons.Outlined.Timer,
                        title = "$minutes Minutes",
                        subtitle = "Playback pauses automatically in $minutes min"
                    ) {
                        SleepTimerManager.startTimer(context, minutes)
                        onDismiss()
                    }
                }

                ActionSheetRow(
                    icon = Icons.Outlined.MusicOff,
                    title = "End of This Song",
                    subtitle = "Stops when currently playing track ends"
                ) {
                    SleepTimerManager.setStopAtEndOfSong(context, true)
                    onDismiss()
                }

                if (remainingSleep != null) {
                    ActionSheetRow(
                        icon = Icons.Outlined.Cancel,
                        title = "Turn Off Sleep Timer",
                        subtitle = "Cancel active timer countdown",
                        iconTint = Color.Red
                    ) {
                        SleepTimerManager.cancelTimer()
                        onDismiss()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showNewPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showNewPlaylistDialog = false },
            containerColor = Color(0xFF181A28),
            title = { Text("New Playlist", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    placeholder = { Text("Playlist Name", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VybeVolt
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val created = PlaylistManager.createPlaylist(newPlaylistName)
                        PlaylistManager.addTrackToPlaylist(created.id, track)
                        showNewPlaylistDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VybeVolt)
                ) {
                    Text("Create & Add", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewPlaylistDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun ActionSheetRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = Color.White,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x1FFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VybeVolt)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(badge, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = Color(0xFFA0A5BA), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
