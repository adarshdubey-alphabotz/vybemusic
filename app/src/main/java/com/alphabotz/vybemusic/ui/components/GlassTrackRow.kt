package com.alphabotz.vybemusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.storage.PlaylistManager
import com.alphabotz.vybemusic.ui.theme.VybeVolt

@Composable
fun GlassTrackRow(
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int? = null,
    onPlayNext: (() -> Unit)? = null,
    onAddToQueue: (() -> Unit)? = null,
    onStartJam: (() -> Unit)? = null
) {
    var showActionSheet by remember { mutableStateOf(false) }
    val likedTracks by PlaylistManager.likedTracks.collectAsState()
    val isLiked = remember(likedTracks, track.id) {
        PlaylistManager.isLiked(track.id)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x1AFFFFFF))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        if (index != null) {
            Text(
                text = "%02d".format(index),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.width(26.dp)
            )
        }

        // Squircle Artwork
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = track.artworkUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200" },
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Artist
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artist,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFA0A5BA),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Lossless Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0x26D2F802))
                        .border(0.5.dp, VybeVolt.copy(alpha = 0.5f), RoundedCornerShape(5.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "320k",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = VybeVolt
                    )
                }
            }
        }

        // Heart Like Toggle Button
        IconButton(
            onClick = { PlaylistManager.toggleLike(track) },
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Like",
                tint = if (isLiked) Color(0xFFFF3366) else Color.White.copy(alpha = 0.45f),
                modifier = Modifier.size(18.dp)
            )
        }

        // 3-Dots Action Sheet Opener
        IconButton(
            onClick = { showActionSheet = true },
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }

    if (showActionSheet) {
        TrackActionSheet(
            track = track,
            onDismiss = { showActionSheet = false },
            onPlayNext = { onPlayNext?.invoke() },
            onAddToQueue = { onAddToQueue?.invoke() },
            onStartJam = { onStartJam?.invoke() }
        )
    }
}
