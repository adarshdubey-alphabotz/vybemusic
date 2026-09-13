package com.alphabotz.vybemusic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alphabotz.vybemusic.core.model.Lyrics
import com.alphabotz.vybemusic.ui.theme.VybeTextSecondary
import com.alphabotz.vybemusic.ui.theme.VybeVolt

/**
 * Apple Music Sing inspired real-time karaoke synchronized lyrics view.
 * Features fluid auto-scroll centering the singing line, glowing scale emphasis,
 * and tap-to-seek playback.
 */
@Composable
fun KaraokeLyricsView(
    lyrics: Lyrics?,
    activeLineIndex: Int,
    onSeekToLine: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (lyrics == null || (lyrics.lines.isEmpty() && lyrics.plainLyrics.isBlank())) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🎵",
                    fontSize = 36.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Instrumental / Lyrics unavailable for this track",
                    color = VybeTextSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    if (!lyrics.isSynced && lyrics.plainLyrics.isNotBlank()) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 120.dp, horizontal = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = lyrics.plainLyrics,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 32.sp
                )
            }
        }
        return
    }

    val listState = rememberLazyListState()

    // Smooth auto-scroll keeping the active line centered in viewport
    LaunchedEffect(activeLineIndex) {
        if (activeLineIndex in lyrics.lines.indices) {
            val targetIndex = (activeLineIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(
                index = targetIndex,
                scrollOffset = 0
            )
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(top = 100.dp, bottom = 180.dp, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(26.dp),
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(lyrics.lines) { index, line ->
            val isActive = index == activeLineIndex

            val textColor by animateColorAsState(
                targetValue = if (isActive) VybeVolt else Color.White.copy(alpha = 0.25f),
                animationSpec = tween(350),
                label = "lyricColor"
            )

            val textScale by animateFloatAsState(
                targetValue = if (isActive) 1.08f else 0.95f,
                animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
                label = "lyricScale"
            )

            Text(
                text = line.text,
                color = textColor,
                fontSize = if (isActive) 28.sp else 22.sp,
                fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold,
                lineHeight = 38.sp,
                modifier = Modifier
                    .scale(textScale)
                    .fillMaxWidth()
                    .clickable {
                        onSeekToLine(line.startTimeMs)
                    }
            )
        }
    }
}
