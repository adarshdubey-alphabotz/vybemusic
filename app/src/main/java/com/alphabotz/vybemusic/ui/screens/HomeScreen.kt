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
import com.alphabotz.vybemusic.core.storage.PlaylistManager
import com.alphabotz.vybemusic.core.storage.UserProfileManager
import com.alphabotz.vybemusic.ui.components.GlassTrackRow
import com.alphabotz.vybemusic.ui.theme.*

@Composable
fun HomeScreen(
    onTrackSelect: (Track, List<Track>) -> Unit,
    onOpenJam: () -> Unit,
    onOpenSearch: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenArtistPicker: () -> Unit = {},
    onPlayNext: (Track) -> Unit = {},
    onAddToQueue: (Track) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by UserProfileManager.profile.collectAsState()
    val likedTracks by PlaylistManager.likedTracks.collectAsState()

    var tracks by remember { mutableStateOf(VybeMusicEngine.getInitialSeedTracks()) }
    var isLoading by remember { mutableStateOf(false) }

    val filterChips = listOf("All", "🔥 Trending", "⚡ New Release", "🎧 Lo-Fi", "🌙 Night Drive", "🧠 Focus")
    var selectedFilter by remember { mutableStateOf(filterChips.first()) }

    LaunchedEffect(selectedFilter, profile.favoriteArtists) {
        if (selectedFilter == "All") {
            isLoading = true
            val curated = VybeMusicEngine.getCuratedFeedForArtists(profile.favoriteArtists)
            if (curated.isNotEmpty()) {
                tracks = curated
            }
            isLoading = false
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
                .height(340.dp)
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
            contentPadding = PaddingValues(bottom = 140.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Header: Avatar + Search + Jam + Profile
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    // Clickable Avatar to view YouTube Music Profile
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, VybeVolt.copy(alpha = 0.8f), CircleShape)
                            .clickable { onOpenProfile() }
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

            // Big Bold Greeting + Tune Taste Pill
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Hi, ${profile.name}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.clickable { onOpenProfile() }
                    )

                    // Taste Tuning Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x26D2F802))
                            .border(1.dp, VybeVolt.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .clickable { onOpenArtistPicker() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Tune Taste",
                                tint = VybeVolt,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (profile.favoriteArtists.isNotEmpty()) "${profile.favoriteArtists.size} Artists" else "Tune Taste",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VybeVolt
                            )
                        }
                    }
                }
            }

            // Onboarding Banner if no artists chosen yet
            if (profile.favoriteArtists.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF2B1055), Color(0xFF591A80))
                                )
                            )
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                            .clickable { onOpenArtistPicker() }
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Personalize Your Feed",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Pick 3+ favorite artists to tune your mixes & auto-queue",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(VybeVolt)
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("Choose →", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 10.dp)
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
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }
            }

            // Liked Songs Quick Card (if user has liked songs)
            if (likedTracks.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF4A0E4E), Color(0xFF260829))
                                )
                            )
                            .border(1.dp, Color(0x33FF3366), RoundedCornerShape(20.dp))
                            .clickable { onTrackSelect(likedTracks.first(), likedTracks) }
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFFF3366), Color(0xFFFF6B8B))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Liked Songs", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("${likedTracks.size} songs • Tap to shuffle play", fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(VybeVolt),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play Liked", tint = Color.Black, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }

            // Featured Hero Card: Discover Weekly
            item {
                Text(
                    text = "Curated & trending",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 12.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color(0xFFC7B8E8))
                        .clickable {
                            if (tracks.isNotEmpty()) {
                                onTrackSelect(tracks.first(), tracks)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Discover weekly",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E1B2E),
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (profile.favoriteArtists.isNotEmpty()) {
                                    "Curated from ${profile.favoriteArtists.take(3).joinToString(", ")}"
                                } else {
                                    "Studio lossless master audio streams"
                                },
                                fontSize = 13.sp,
                                color = Color(0xFF3B3754),
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2C2442)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = Color(0xFF2C2442),
                                    modifier = Modifier.size(24.dp)
                                )

                                Icon(
                                    imageVector = Icons.Outlined.Download,
                                    contentDescription = "Download",
                                    tint = Color(0xFF2C2442),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Hero Artist Image
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(18.dp))
                        ) {
                            AsyncImage(
                                model = tracks.firstOrNull()?.artworkUrl ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Section Header: Top Daily Tracks
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 26.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (profile.favoriteArtists.isNotEmpty()) "Made for ${profile.name}" else "Top daily tracks",
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

            // Track Items List with Play Next, Add to Queue & Liked Songs support
            items(tracks, key = { it.id }) { track ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                    GlassTrackRow(
                        track = track,
                        onClick = { onTrackSelect(track, tracks) },
                        onPlayNext = { onPlayNext(track) },
                        onAddToQueue = { onAddToQueue(track) },
                        onStartJam = onOpenJam
                    )
                }
            }
        }
    }
}
