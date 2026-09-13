package com.alphabotz.vybemusic.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.network.VybeMusicEngine
import com.alphabotz.vybemusic.ui.components.GlassTrackRow
import com.alphabotz.vybemusic.ui.theme.*

data class GenreCategory(
    val title: String,
    val subtitle: String,
    val searchQuery: String,
    val gradient: Brush
)

@Composable
fun ExploreScreen(
    onTrackSelect: (Track, List<Track>) -> Unit,
    onPlayNext: (Track) -> Unit = {},
    onAddToQueue: (Track) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val categories = remember {
        listOf(
            GenreCategory(
                "Top 50 Global Hits",
                "Most played worldwide today",
                "top billboard trending hits",
                Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)))
            ),
            GenreCategory(
                "Hip-Hop & 90s Rap",
                "Tupac, Eminem, Biggie, West Coast bangers",
                "hip hop rap west coast hits",
                Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))
            ),
            GenreCategory(
                "Punjabi Heat",
                "AP Dhillon, Diljit, Karan Aujla, Shubh",
                "punjabi trending hits",
                Brush.linearGradient(listOf(Color(0xFFFF5722), Color(0xFFFF9800)))
            ),
            GenreCategory(
                "Bollywood Romance",
                "Arijit Singh, Pritam, Vishal Mishra",
                "bollywood romantic hits",
                Brush.linearGradient(listOf(Color(0xFFE91E63), Color(0xFF9C27B0)))
            ),
            GenreCategory(
                "Cyberpunk Lo-Fi",
                "Synthwave, chill lofi, night drive beats",
                "lofi hip hop chill beats",
                Brush.linearGradient(listOf(Color(0xFF00F0FF), Color(0xFF3F51B5)))
            ),
            GenreCategory(
                "Gym Beast Mode",
                "High energy workout, hardstyle, phonk",
                "workout phonk gym beast mode",
                Brush.linearGradient(listOf(Color(0xFFD32F2F), Color(0xFF1976D2)))
            )
        )
    }

    var selectedCategory by remember { mutableStateOf<GenreCategory?>(null) }
    var categoryTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var isLoadingCategory by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCategory) {
        val cat = selectedCategory
        if (cat != null) {
            isLoadingCategory = true
            val results = VybeMusicEngine.searchTracks(cat.searchQuery, page = 1, limit = 25)
            categoryTracks = if (results.isNotEmpty()) results else VybeMusicEngine.getInitialSeedTracks()
            isLoadingCategory = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VybeBackground)
            .statusBarsPadding()
    ) {
        if (selectedCategory == null) {
            // Main Explore Grid
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "Explore & Charts",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Curated stations, global charts & moods",
                        fontSize = 13.sp,
                        color = Color(0xFFA0A5BA),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                }

                items(categories) { cat ->
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { selectedCategory = cat }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(cat.gradient)
                                .padding(20.dp)
                        ) {
                            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                                Text(
                                    text = cat.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = cat.subtitle,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Circular play indicator
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .align(Alignment.CenterEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Open",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Selected Category Playlist View
            val cat = selectedCategory!!
            LazyColumn(
                contentPadding = PaddingValues(bottom = 120.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(cat.gradient)
                            .padding(20.dp)
                    ) {
                        IconButton(
                            onClick = { selectedCategory = null },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Column(modifier = Modifier.align(Alignment.BottomStart)) {
                            Text(
                                text = cat.title,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${categoryTracks.size} Tracks • Curated for You",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        // Play All Button
                        if (categoryTracks.isNotEmpty()) {
                            Button(
                                onClick = { onTrackSelect(categoryTracks.first(), categoryTracks) },
                                colors = ButtonDefaults.buttonColors(containerColor = VybeVolt),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play All", tint = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Play All", color = Color.Black, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                if (isLoadingCategory) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = VybeVolt)
                        }
                    }
                } else {
                    items(categoryTracks) { track ->
                        GlassTrackRow(
                            track = track,
                            onClick = { onTrackSelect(track, categoryTracks) },
                            onPlayNext = { onPlayNext(track) },
                            onAddToQueue = { onAddToQueue(track) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
