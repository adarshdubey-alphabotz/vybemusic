package com.alphabotz.vybemusic.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.storage.UserProfileManager
import com.alphabotz.vybemusic.ui.theme.VybeBackground
import com.alphabotz.vybemusic.ui.theme.VybeVolt

data class ArtistItem(
    val id: String,
    val name: String,
    val genre: String,
    val category: String,
    val imageUrl: String
)

val PRESET_ARTISTS = listOf(
    // Hip-Hop / Rap
    ArtistItem("2pac", "2Pac", "Hip-Hop / 90s Legend", "Hip-Hop", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=300"),
    ArtistItem("weeknd", "The Weeknd", "R&B / Synthpop", "Pop", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"),
    ArtistItem("eminem", "Eminem", "Hip-Hop / Rap", "Hip-Hop", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300"),
    ArtistItem("travis", "Travis Scott", "Trap / Psychedelic Rap", "Hip-Hop", "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=300"),
    ArtistItem("drake", "Drake", "Hip-Hop / Melodic", "Hip-Hop", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300"),
    ArtistItem("kendrick", "Kendrick Lamar", "Conscious Rap", "Hip-Hop", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300"),
    ArtistItem("wiz", "Wiz Khalifa", "Hip-Hop / Chill Rap", "Hip-Hop", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300"),
    ArtistItem("post", "Post Malone", "Pop / Rap / Rock", "Pop", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=300"),

    // Pop & Global
    ArtistItem("charlie", "Charlie Puth", "Pop / Soul", "Pop", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300"),
    ArtistItem("rihanna", "Rihanna", "Pop / R&B", "Pop", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300"),
    ArtistItem("taylor", "Taylor Swift", "Pop / Country", "Pop", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300"),
    ArtistItem("billie", "Billie Eilish", "Alt-Pop / Dark Pop", "Pop", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300"),
    ArtistItem("bruno", "Bruno Mars", "Funk / Pop / R&B", "Pop", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=300"),
    ArtistItem("dualipa", "Dua Lipa", "Disco Pop / Dance", "Pop", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300"),

    // Punjabi Hits
    ArtistItem("ap", "AP Dhillon", "Punjabi / Indo-Western", "Punjabi", "https://c.saavncdn.com/890/Excuses-English-2021-20210930112054-500x500.jpg"),
    ArtistItem("sidhu", "Sidhu Moose Wala", "Punjabi Rap / Gangsta", "Punjabi", "https://c.saavncdn.com/209/MoonChild-Era-Punjabi-2021-20240715073449-500x500.jpg"),
    ArtistItem("diljit", "Diljit Dosanjh", "Punjabi / Pop", "Punjabi", "https://c.saavncdn.com/209/MoonChild-Era-Punjabi-2021-20240715073449-500x500.jpg"),
    ArtistItem("aujla", "Karan Aujla", "Punjabi / Hip-Hop", "Punjabi", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300"),
    ArtistItem("shubh", "Shubh", "Punjabi / Lo-Fi Rap", "Punjabi", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300"),
    ArtistItem("divine", "DIVINE", "Gully Rap / Hip-Hop", "Hip-Hop", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=300"),

    // Bollywood & Romantic
    ArtistItem("arijit", "Arijit Singh", "Romantic / Melodies", "Bollywood", "https://c.saavncdn.com/871/Brahmastra-Original-Motion-Picture-Soundtrack-Hindi-2022-20221006155213-500x500.jpg"),
    ArtistItem("atif", "Atif Aslam", "Romantic / Rock", "Bollywood", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=300"),
    ArtistItem("pritam", "Pritam", "Bollywood / Chartbusters", "Bollywood", "https://c.saavncdn.com/871/Brahmastra-Original-Motion-Picture-Soundtrack-Hindi-2022-20221006155213-500x500.jpg"),
    ArtistItem("vishal", "Vishal Mishra", "Soulful / Bollywood", "Bollywood", "https://c.saavncdn.com/092/ANIMAL-Hindi-2023-20260724191152-500x500.jpg"),
    ArtistItem("kk", "KK", "Nostalgic / Rock Melodies", "Bollywood", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300"),

    // Rock & Electronic & Lo-Fi
    ArtistItem("coldplay", "Coldplay", "Alt Rock / Pop", "Rock", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300"),
    ArtistItem("linkin", "Linkin Park", "Nu Metal / Rock", "Rock", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"),
    ArtistItem("daft", "Daft Punk", "Electronic / French Touch", "EDM", "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=300"),
    ArtistItem("lofi", "Lofi Beats", "Chillhop / Study Vibes", "Lo-Fi", "https://c.saavncdn.com/670/Faded-Instrumental-2022-20260324143104-500x500.jpg")
)

@Composable
fun ArtistSelectionScreen(
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by UserProfileManager.profile.collectAsState()
    val selectedArtists = remember { mutableStateListOf<String>().apply { addAll(profile.favoriteArtists) } }

    val categories = listOf("All", "Hip-Hop", "Pop", "Punjabi", "Bollywood", "Rock", "Lo-Fi")
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredArtists = remember(selectedCategory, searchQuery) {
        PRESET_ARTISTS.filter { artist ->
            val matchesCategory = (selectedCategory == "All") || (artist.category == selectedCategory)
            val matchesQuery = searchQuery.isBlank() ||
                    artist.name.contains(searchQuery, ignoreCase = true) ||
                    artist.genre.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val isMinRequirementMet = selectedArtists.size >= 3

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VybeBackground)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp)
        ) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text(
                    text = "Pick 3 or more artists you love.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vybe Music personalizes your Home feed, Daily Mixes, and Up Next queue to your exact taste.",
                    fontSize = 13.sp,
                    color = Color(0xFFA0A5BA),
                    lineHeight = 18.sp
                )
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x1FFFFFFF))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Search artists, genres...", color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) VybeVolt else Color(0x1FFFFFFF))
                            .border(1.dp, if (isSelected) VybeVolt else Color(0x2EFFFFFF), RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }

            // Artists Grid (3 Columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredArtists, key = { it.id }) { artist ->
                    val isSelected = selectedArtists.contains(artist.name)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                if (isSelected) {
                                    selectedArtists.remove(artist.name)
                                } else {
                                    selectedArtists.add(artist.name)
                                }
                            }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isSelected) 3.5.dp else 1.5.dp,
                                    color = if (isSelected) VybeVolt else Color.White.copy(alpha = 0.15f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = artist.imageUrl,
                                contentDescription = artist.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Volt Checkmark Overlay when selected
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.45f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = VybeVolt,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = artist.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) VybeVolt else Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = artist.genre,
                            fontSize = 10.sp,
                            color = Color(0xFFA0A5BA),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Floating Bottom Finish Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = {
                    if (isMinRequirementMet) {
                        UserProfileManager.saveArtists(selectedArtists.toSet())
                        onCompleted()
                    }
                },
                enabled = isMinRequirementMet,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VybeVolt,
                    disabledContainerColor = Color(0x33FFFFFF)
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isMinRequirementMet) {
                            "Continue (${selectedArtists.size} selected) →"
                        } else {
                            "Select at least ${3 - selectedArtists.size} more"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isMinRequirementMet) Color.Black else Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
