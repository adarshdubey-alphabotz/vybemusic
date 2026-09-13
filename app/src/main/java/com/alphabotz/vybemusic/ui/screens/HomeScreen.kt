package com.alphabotz.vybemusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.network.VybeMusicEngine
import com.alphabotz.vybemusic.ui.theme.*

/**
 * YouTube Music inspired Home Screen with Quick Picks, Trending, and Mood filters.
 */
@Composable
fun HomeScreen(
    onTrackSelect: (Track, List<Track>) -> Unit,
    onOpenJam: () -> Unit,
    modifier: Modifier = Modifier
) {
    var trendingTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val moodChips = listOf("⚡ Energetic", "🧠 Focus / Study", "🌙 Night Drive", "🔥 Trending", "🧘 Relax", "🎧 Lo-Fi")
    var selectedMood by remember { mutableStateOf(moodChips.first()) }

    LaunchedEffect(Unit) {
        trendingTracks = VybeMusicEngine.getTrendingTracks()
        isLoading = false
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 120.dp),
        modifier = modifier
            .fillMaxSize()
            .background(VybeBackground)
    ) {
        // Header
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(VybePrimary, VybeAccent))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("V", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Vybe Music",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }

                // Vybe Jam Quick Action
                IconButton(
                    onClick = onOpenJam,
                    modifier = Modifier
                        .background(VybeSurfaceElevated, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Vybe Jam",
                        tint = VybePrimary
                    )
                }
            }
        }

        // Mood & Activity Chips (YouTube Music style)
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                items(moodChips) { mood ->
                    val isSelected = mood == selectedMood
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) VybePrimary else VybeSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) VybePrimary else VybeSurfaceBorder),
                        modifier = Modifier.clickable { selectedMood = mood }
                    ) {
                        Text(
                            text = mood,
                            color = if (isSelected) Color.White else VybeTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Quick Picks Section (YouTube Music style)
        item {
            SectionHeader(title = "Quick Picks", subtitle = "START RADIO FROM A SONG")
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VybePrimary)
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 28.dp)
                ) {
                    items(trendingTracks.take(8)) { track ->
                        QuickPickCard(track = track, onClick = { onTrackSelect(track, trendingTracks) })
                    }
                }
            }
        }

        // Trending Hits Section
        item {
            SectionHeader(title = "Trending Now", subtitle = "MOST PLAYED WORLDWIDE")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                trendingTracks.drop(8).take(10).forEachIndexed { index, track ->
                    TrackRowItem(
                        index = index + 1,
                        track = track,
                        onClick = { onTrackSelect(track, trendingTracks) }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = subtitle,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = VybeTextTertiary,
            letterSpacing = 1.sp
        )
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun QuickPickCard(track: Track, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = track.artworkUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300" },
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Play overlay on bottom right
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = track.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = track.artist,
            fontSize = 12.sp,
            color = VybeTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TrackRowItem(index: Int, track: Track, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VybeSurface)
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Text(
            text = "%02d".format(index),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = VybeTextTertiary,
            modifier = Modifier.width(28.dp)
        )

        AsyncImage(
            model = track.artworkUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=150" },
            contentDescription = track.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist,
                fontSize = 13.sp,
                color = VybeTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = "320k",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = VybeEmerald,
            modifier = Modifier
                .background(VybeSurfaceElevated, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
