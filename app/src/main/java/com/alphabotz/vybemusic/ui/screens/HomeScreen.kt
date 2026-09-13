package com.alphabotz.vybemusic.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.network.VybeMusicEngine
import com.alphabotz.vybemusic.core.storage.UserProfileManager
import com.alphabotz.vybemusic.ui.components.GlassTrackRow
import com.alphabotz.vybemusic.ui.theme.*

@Composable
fun HomeScreen(
    onTrackSelect: (Track, List<Track>) -> Unit,
    onOpenJam: () -> Unit,
    onOpenSearch: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by UserProfileManager.profile.collectAsState()
    var showProfileDialog by remember { mutableStateOf(false) }

    var tracks by remember { mutableStateOf(VybeMusicEngine.getInitialSeedTracks()) }
    var isLoading by remember { mutableStateOf(false) }

    val filterChips = listOf("All", "⚡ New Release", "🔥 Trending", "🎧 Lo-Fi", "🌙 Night Drive", "🧠 Focus")
    var selectedFilter by remember { mutableStateOf(filterChips.first()) }

    LaunchedEffect(selectedFilter) {
        if (selectedFilter == "All") {
            val liveTracks = VybeMusicEngine.getTrendingTracks()
            if (liveTracks.isNotEmpty()) {
                tracks = liveTracks
            }
        } else {
            isLoading = true
            val filtered = VybeMusicEngine.getTrendingTracks(selectedFilter)
            if (filtered.isNotEmpty()) {
                tracks = filtered
            }
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VybeBackground)
    ) {
        // Ethereal top ambient glow bloom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x558B5CF6),
                            Color(0x25EC4899),
                            Color.Transparent
                        ),
                        center = Offset(200f, 100f),
                        radius = 800f
                    )
                )
        )

        LazyColumn(
            contentPadding = PaddingValues(bottom = 120.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Header: Avatar + Glass Action Pills
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    // Clickable Avatar to edit name/photo
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, VybeVolt.copy(alpha = 0.8f), CircleShape)
                            .clickable { showProfileDialog = true }
                    ) {
                        AsyncImage(
                            model = profile.avatarUrl,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Glass Action Buttons: Search & Vybe Jam
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search Glass Pill
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0x26FFFFFF))
                                .border(1.dp, Color(0x33FFFFFF), CircleShape)
                                .clickable { onOpenSearch() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Vybe Jam Glass Pill
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0x26FFFFFF))
                                .border(1.dp, Color(0x33FFFFFF), CircleShape)
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
                }
            }

            // Big Bold Dynamic Greeting (Tap to edit name)
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clickable { showProfileDialog = true }
                ) {
                    Text(
                        text = "Hi, ${profile.name}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Name",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Filter Chips (Volt Lime Active Pill)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    items(filterChips) { filter ->
                        val isSelected = filter == selectedFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (isSelected) VybeVolt else Color(0x22FFFFFF))
                                .border(
                                    1.dp,
                                    if (isSelected) VybeVolt else Color(0x2EFFFFFF),
                                    RoundedCornerShape(24.dp)
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 18.dp, vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Section 1: Curated & Trending (Hero Card: Discover Weekly Lavender Card)
            item {
                Text(
                    text = "Curated & trending",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )

                // Hero Lavender Pastel Card (Inspiration 1)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(VybeLavender)
                        .clickable {
                            if (tracks.isNotEmpty()) {
                                onTrackSelect(tracks.first(), tracks)
                            }
                        }
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Text & Controls
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Discover weekly",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF140A26),
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "The original slow instrumental best playlists.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF4C336D),
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(18.dp))

                            // Action Row: Plum circular Play button + Heart + Download + More
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Deep Plum Circular Play Button
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(VybePlum)
                                        .clickable {
                                            if (tracks.isNotEmpty()) {
                                                onTrackSelect(tracks.first(), tracks)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Discover Weekly",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = Color(0xFF381663),
                                    modifier = Modifier.size(20.dp)
                                )

                                Icon(
                                    imageVector = Icons.Outlined.Download,
                                    contentDescription = "Download",
                                    tint = Color(0xFF381663),
                                    modifier = Modifier.size(20.dp)
                                )

                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = "Options",
                                    tint = Color(0xFF381663),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Right Listener Art
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
                                contentDescription = "Headphones Neon Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Section 2: Top Daily Tracks
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top daily tracks",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "See all",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = VybeVolt,
                        modifier = Modifier.clickable { onOpenSearch() }
                    )
                }
            }

            // Track Items List
            items(tracks) { track ->
                GlassTrackRow(
                    track = track,
                    onClick = { onTrackSelect(track, tracks) }
                )
            }
        }
    }

    // Interactive Edit Profile Dialog
    if (showProfileDialog) {
        var newNameInput by remember { mutableStateOf(profile.name) }
        var selectedAvatarUrl by remember { mutableStateOf(profile.avatarUrl) }

        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            containerColor = Color(0xFF181A28),
            title = {
                Text("Customize Profile", color = Color.White, fontWeight = FontWeight.Black)
            },
            text = {
                Column {
                    Text("Display Name", color = Color(0xFFA0A5BA), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newNameInput,
                        onValueChange = { newNameInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VybeVolt,
                            unfocusedBorderColor = Color(0x4DFFFFFF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Choose Avatar", color = Color(0xFFA0A5BA), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(UserProfileManager.AVATAR_OPTIONS) { avUrl ->
                            val isSelected = avUrl == selectedAvatarUrl
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.5.dp,
                                        if (isSelected) VybeVolt else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { selectedAvatarUrl = avUrl }
                            ) {
                                AsyncImage(
                                    model = avUrl,
                                    contentDescription = "Avatar Option",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        UserProfileManager.updateProfile(newNameInput, selectedAvatarUrl)
                        showProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VybeVolt),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}
