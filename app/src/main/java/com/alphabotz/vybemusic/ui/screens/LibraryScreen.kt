package com.alphabotz.vybemusic.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.storage.DownloadManager
import com.alphabotz.vybemusic.core.storage.Playlist
import com.alphabotz.vybemusic.core.storage.PlaylistManager
import com.alphabotz.vybemusic.core.storage.UserProfileManager
import com.alphabotz.vybemusic.ui.components.GlassTrackRow
import com.alphabotz.vybemusic.ui.theme.*

enum class LibraryTab { PLAYLISTS, LIKED, DOWNLOADED, ARTISTS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onTrackSelect: (Track, List<Track>) -> Unit,
    onPlayNext: (Track) -> Unit = {},
    onAddToQueue: (Track) -> Unit = {},
    onOpenArtistPicker: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.PLAYLISTS) }

    val likedTracks by PlaylistManager.likedTracks.collectAsState()
    val playlists by PlaylistManager.playlists.collectAsState()
    val downloadedTracks by DownloadManager.downloadedTracks.collectAsState()
    val profile by UserProfileManager.profile.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedPlaylistDetail by remember { mutableStateOf<Playlist?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Library",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(VybeSurfaceElevated)
                    .border(1.dp, VybeSurfaceBorder, CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Playlist", tint = VybePrimary)
            }
        }

        // Tab Selector Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                LibraryTab.PLAYLISTS to "Playlists",
                LibraryTab.LIKED to "Liked (${likedTracks.size})",
                LibraryTab.DOWNLOADED to "Offline (${downloadedTracks.size})",
                LibraryTab.ARTISTS to "Artists"
            ).forEach { (tab, label) ->
                val isSelected = selectedTab == tab
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) VybePrimary else VybeSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) VybePrimary else VybeSurfaceBorder
                    ),
                    modifier = Modifier.clickable { selectedTab = tab }
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            LibraryTab.PLAYLISTS -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 140.dp)
                ) {
                    // Liked Songs Card
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF1E1035),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9C27B0).copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (likedTracks.isNotEmpty()) {
                                        onTrackSelect(likedTracks.first(), likedTracks)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Brush.linearGradient(listOf(Color(0xFF8A2BE2), Color(0xFFFF1493)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Liked Songs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                    Text("${likedTracks.size} songs in collection", color = VybeTextSecondary, fontSize = 13.sp)
                                }
                                if (likedTracks.isNotEmpty()) {
                                    IconButton(
                                        onClick = { onTrackSelect(likedTracks.first(), likedTracks) },
                                        modifier = Modifier.clip(CircleShape).background(VybePrimary)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
                                    }
                                }
                            }
                        }
                    }

                    // User Playlists
                    items(playlists) { playlist ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = VybeSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VybeSurfaceBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPlaylistDetail = playlist }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = playlist.coverUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300" },
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(54.dp).clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(playlist.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    Text("${playlist.tracks.size} tracks • ${playlist.description}", color = VybeTextSecondary, fontSize = 12.sp, maxLines = 1)
                                }
                                if (playlist.tracks.isNotEmpty()) {
                                    IconButton(onClick = { onTrackSelect(playlist.tracks.first(), playlist.tracks) }) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = VybePrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            LibraryTab.LIKED -> {
                if (likedTracks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No liked songs yet! Tap ❤️ on any song to save it.", color = VybeTextSecondary, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 140.dp)
                    ) {
                        itemsIndexed(likedTracks) { index, track ->
                            GlassTrackRow(
                                index = index + 1,
                                track = track,
                                onClick = { onTrackSelect(track, likedTracks) },
                                onPlayNext = { onPlayNext(track) },
                                onAddToQueue = { onAddToQueue(track) }
                            )
                        }
                    }
                }
            }

            LibraryTab.DOWNLOADED -> {
                if (downloadedTracks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = VybeTextSecondary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No downloaded songs yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Tap 3 dots on any song and select Download for offline playback", color = VybeTextSecondary, fontSize = 13.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 140.dp)
                    ) {
                        itemsIndexed(downloadedTracks) { index, track ->
                            GlassTrackRow(
                                index = index + 1,
                                track = track,
                                onClick = { onTrackSelect(track, downloadedTracks) },
                                onPlayNext = { onPlayNext(track) },
                                onAddToQueue = { onAddToQueue(track) }
                            )
                        }
                    }
                }
            }

            LibraryTab.ARTISTS -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 140.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = VybeSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VybeSurfaceBorder),
                            modifier = Modifier.fillMaxWidth().clickable { onOpenArtistPicker() }
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Stars, contentDescription = null, tint = VybePrimary, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Tune Your Taste", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("${profile.favoriteArtists.size} favorite artists selected", color = VybeTextSecondary, fontSize = 12.sp)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = VybeTextSecondary)
                            }
                        }
                    }

                    items(PRESET_ARTISTS.filter { profile.favoriteArtists.contains(it.id) || profile.favoriteArtists.contains(it.name) }) { artist ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = VybeSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VybeSurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = artist.imageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(50.dp).clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(artist.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    Text(artist.genre, color = VybeTextSecondary, fontSize = 12.sp)
                                }
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VybePrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Playlist Dialog
    if (showCreateDialog) {
        var playlistName by remember { mutableStateOf("") }
        var playlistDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Playlist", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        label = { Text("Playlist Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VybePrimary,
                            unfocusedBorderColor = VybeSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = playlistDesc,
                        onValueChange = { playlistDesc = it },
                        label = { Text("Description (Optional)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VybePrimary,
                            unfocusedBorderColor = VybeSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (playlistName.isNotBlank()) {
                            PlaylistManager.createPlaylist(playlistName.trim(), playlistDesc.trim())
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VybePrimary)
                ) {
                    Text("Create", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = VybeTextSecondary)
                }
            },
            containerColor = Color(0xFF1B1B26)
        )
    }

    // Playlist Details Bottom Sheet
    selectedPlaylistDetail?.let { playlist ->
        ModalBottomSheet(
            onDismissRequest = { selectedPlaylistDetail = null },
            containerColor = Color(0xFF14141E)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(playlist.title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                Text("${playlist.tracks.size} tracks • ${playlist.description}", color = VybeTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(16.dp))

                if (playlist.tracks.isEmpty()) {
                    Text("No tracks in this playlist yet. Tap 3 dots on any song to add it!", color = VybeTextSecondary, fontSize = 14.sp)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxHeight(0.6f)
                    ) {
                        itemsIndexed(playlist.tracks) { index, track ->
                            GlassTrackRow(
                                index = index + 1,
                                track = track,
                                onClick = { onTrackSelect(track, playlist.tracks) },
                                onPlayNext = { onPlayNext(track) },
                                onAddToQueue = { onAddToQueue(track) }
                            )
                        }
                    }
                }
            }
        }
    }
}
