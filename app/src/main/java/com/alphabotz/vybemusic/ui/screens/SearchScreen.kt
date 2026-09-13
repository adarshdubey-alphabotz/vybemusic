package com.alphabotz.vybemusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.network.VybeMusicEngine
import com.alphabotz.vybemusic.ui.theme.*
import com.alphabotz.vybemusic.ui.components.GlassTrackRow
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onTrackSelect: (Track, List<Track>) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Track>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    // Debounced real-time search
    LaunchedEffect(searchQuery) {
        if (searchQuery.trim().length >= 2) {
            isSearching = true
            delay(400) // 400ms debounce
            val results = VybeMusicEngine.searchTracks(searchQuery.trim())
            searchResults = results
            isSearching = false
        } else {
            searchResults = emptyList()
            isSearching = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VybeBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Search",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Songs, artists, podcasts, or albums", color = VybeTextTertiary, fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = VybePrimary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = VybeTextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = VybeSurfaceElevated,
                unfocusedContainerColor = VybeSurfaceElevated,
                focusedBorderColor = VybePrimary,
                unfocusedBorderColor = VybeSurfaceBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isSearching) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VybePrimary)
            }
        } else if (searchResults.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(searchResults) { index, track ->
                    GlassTrackRow(
                        index = index + 1,
                        track = track,
                        onClick = { onTrackSelect(track, searchResults) }
                    )
                }
            }
        } else if (searchQuery.isNotBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No songs found for '$searchQuery'",
                    color = VybeTextSecondary,
                    fontSize = 15.sp
                )
            }
        } else {
            // Search Categories suggestion
            Text(
                text = "Explore Categories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            val categories = listOf("Pop", "Hip-Hop", "Punjabi", "Bollywood", "Lo-Fi", "Electronic", "Rock", "R&B")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.chunked(2).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = VybeSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, VybeSurfaceBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Text(cat, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
