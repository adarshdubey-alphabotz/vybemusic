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
    ArtistItem("2pac", "2Pac", "Hip-Hop / 90s Legend", "Hip-Hop", "https://cdn-images.dzcdn.net/images/artist/dc2743d871b5935004292eed2cd55f68/500x500-000000-80-0-0.jpg"),
    ArtistItem("weeknd", "The Weeknd", "R&B / Synthpop", "Pop", "https://cdn-images.dzcdn.net/images/artist/581693b4724a7fcfa754455101e13a44/500x500-000000-80-0-0.jpg"),
    ArtistItem("eminem", "Eminem", "Hip-Hop / Rap", "Hip-Hop", "https://cdn-images.dzcdn.net/images/artist/7fa738468c9a73ff98c1e1b78d622b81/500x500-000000-80-0-0.jpg"),
    ArtistItem("travis", "Travis Scott", "Trap / Psychedelic Rap", "Hip-Hop", "https://cdn-images.dzcdn.net/images/artist/8d8316146026d7e6ce377e314536df62/500x500-000000-80-0-0.jpg"),
    ArtistItem("drake", "Drake", "Hip-Hop / Melodic", "Hip-Hop", "https://cdn-images.dzcdn.net/images/artist/1051e7fd110f9d3e5e88cdc69c5f227b/500x500-000000-80-0-0.jpg"),
    ArtistItem("kendrick", "Kendrick Lamar", "Conscious Rap", "Hip-Hop", "https://cdn-images.dzcdn.net/images/artist/be0a7c550567f4af0ed202d7235b74d6/500x500-000000-80-0-0.jpg"),
    ArtistItem("wiz", "Wiz Khalifa", "Hip-Hop / Chill Rap", "Hip-Hop", "https://cdn-images.dzcdn.net/images/artist/a1dc970ad2ad6afa42580c692b8a8a8d/500x500-000000-80-0-0.jpg"),
    ArtistItem("post", "Post Malone", "Pop / Rap / Rock", "Pop", "https://cdn-images.dzcdn.net/images/artist/a5a8cca44e7eab2db7d44e039bed2574/500x500-000000-80-0-0.jpg"),

    // Pop & Global
    ArtistItem("charlie", "Charlie Puth", "Pop / Soul", "Pop", "https://cdn-images.dzcdn.net/images/artist/c355b366638e0c5f20f4b265f0f12646/500x500-000000-80-0-0.jpg"),
    ArtistItem("rihanna", "Rihanna", "Pop / R&B", "Pop", "https://cdn-images.dzcdn.net/images/artist/6fcfaac74179ff42d7aff076a5265d96/500x500-000000-80-0-0.jpg"),
    ArtistItem("taylor", "Taylor Swift", "Pop / Country", "Pop", "https://cdn-images.dzcdn.net/images/artist/e528e270424103b527f8a27ac625563b/500x500-000000-80-0-0.jpg"),
    ArtistItem("billie", "Billie Eilish", "Alt-Pop / Dark Pop", "Pop", "https://cdn-images.dzcdn.net/images/artist/8eab1a9a644889aabaca1e193e05f984/500x500-000000-80-0-0.jpg"),
    ArtistItem("bruno", "Bruno Mars", "Funk / Pop / R&B", "Pop", "https://cdn-images.dzcdn.net/images/artist/90f0b5b11df4f87ee878f38569b5995b/500x500-000000-80-0-0.jpg"),
    ArtistItem("dualipa", "Dua Lipa", "Disco Pop / Dance", "Pop", "https://cdn-images.dzcdn.net/images/artist/877872aaf75694f11d53c318700ab2b5/500x500-000000-80-0-0.jpg"),

    // Punjabi Hits
    ArtistItem("ap", "AP Dhillon", "Punjabi / Indo-Western", "Punjabi", "https://cdn-images.dzcdn.net/images/artist/52594ac9fa763dc163ed13d21cb130ec/500x500-000000-80-0-0.jpg"),
    ArtistItem("sidhu", "Sidhu Moose Wala", "Punjabi Rap / Gangsta", "Punjabi", "https://cdn-images.dzcdn.net/images/artist/fb1def876c43cc16738bfd6ad3d1dcd9/500x500-000000-80-0-0.jpg"),
    ArtistItem("diljit", "Diljit Dosanjh", "Punjabi / Pop", "Punjabi", "https://cdn-images.dzcdn.net/images/artist/79b85e695e0ca6529e56bf3b628e92bd/500x500-000000-80-0-0.jpg"),
    ArtistItem("aujla", "Karan Aujla", "Punjabi / Hip-Hop", "Punjabi", "https://cdn-images.dzcdn.net/images/artist/a91a1d5ea91e85e4f0966569b50e8d6a/500x500-000000-80-0-0.jpg"),
    ArtistItem("shubh", "Shubh", "Punjabi / Lo-Fi Rap", "Punjabi", "https://cdn-images.dzcdn.net/images/artist/66c1e15679704beb01c912eb6668de14/500x500-000000-80-0-0.jpg"),
    ArtistItem("divine", "DIVINE", "Gully Rap / Hip-Hop", "Hip-Hop", "https://cdn-images.dzcdn.net/images/artist/343c93eb51eb5abb8c1e43fe371be1d1/500x500-000000-80-0-0.jpg"),

    // Bollywood & Romantic
    ArtistItem("arijit", "Arijit Singh", "Romantic / Melodies", "Bollywood", "https://cdn-images.dzcdn.net/images/artist/ac5350cff290edd5b69fa584b8b1bd4f/500x500-000000-80-0-0.jpg"),
    ArtistItem("atif", "Atif Aslam", "Romantic / Rock", "Bollywood", "https://cdn-images.dzcdn.net/images/artist/0ea90444148fff9c11d77f06a344724e/500x500-000000-80-0-0.jpg"),
    ArtistItem("pritam", "Pritam", "Bollywood / Chartbusters", "Bollywood", "https://cdn-images.dzcdn.net/images/artist/d4914ccd414067cd5e2c108867079a85/500x500-000000-80-0-0.jpg"),
    ArtistItem("vishal", "Vishal Mishra", "Soulful / Bollywood", "Bollywood", "https://cdn-images.dzcdn.net/images/artist/6125247795ee1f0b64253a1e993e7b0e/500x500-000000-80-0-0.jpg"),
    ArtistItem("kk", "KK", "Nostalgic / Rock Melodies", "Bollywood", "https://cdn-images.dzcdn.net/images/artist/c4d613b651e2172622383fef15bed657/500x500-000000-80-0-0.jpg"),

    // Rock & Electronic & Lo-Fi
    ArtistItem("coldplay", "Coldplay", "Alt Rock / Pop", "Rock", "https://cdn-images.dzcdn.net/images/artist/3087954bca22f306324912e5ac8375c3/500x500-000000-80-0-0.jpg"),
    ArtistItem("linkin", "Linkin Park", "Nu Metal / Rock", "Rock", "https://cdn-images.dzcdn.net/images/artist/4886905210739af3438990897bad3a98/500x500-000000-80-0-0.jpg"),
    ArtistItem("daft", "Daft Punk", "Electronic / French Touch", "EDM", "https://cdn-images.dzcdn.net/images/artist/638e69b9caaf9f9f3f8826febea7b543/500x500-000000-80-0-0.jpg"),
    ArtistItem("lofi", "Lofi Beats", "Chillhop / Study Vibes", "Lo-Fi", "https://cdn-images.dzcdn.net/images/artist/e1fa61f839957d88511609832a416a03/500x500-000000-80-0-0.jpg")
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
