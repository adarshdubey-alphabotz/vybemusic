package com.alphabotz.vybemusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alphabotz.vybemusic.ui.theme.*

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier
) {
    val genres = listOf(
        Pair("Top 50 Global", Brush.linearGradient(listOf(VybePrimary, VybeAccent))),
        Pair("Punjabi Hits", Brush.linearGradient(listOf(Color(0xFFFF5722), Color(0xFFFF9800)))),
        Pair("Bollywood Romance", Brush.linearGradient(listOf(Color(0xFFE91E63), Color(0xFF9C27B0)))),
        Pair("Cyberpunk Lo-Fi", Brush.linearGradient(listOf(VybeCyan, Color(0xFF3F51B5)))),
        Pair("Gym Beast Mode", Brush.linearGradient(listOf(Color(0xFFD32F2F), Color(0xFF1976D2)))),
        Pair("Deep Focus & Study", Brush.linearGradient(listOf(Color(0xFF009688), VybeEmerald)))
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .background(VybeBackground)
            .statusBarsPadding()
    ) {
        item {
            Text(
                text = "Explore & Charts",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Hand-picked stations, global charts & moods",
                fontSize = 13.sp,
                color = VybeTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
        }

        items(genres.size) { index ->
            val (title, gradient) = genres[index]
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .padding(20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
