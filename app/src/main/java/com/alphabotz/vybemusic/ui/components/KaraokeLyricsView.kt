package com.alphabotz.vybemusic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alphabotz.vybemusic.core.model.Lyrics
import com.alphabotz.vybemusic.ui.theme.VybeTextSecondary

/**
 * Apple Music inspired real-time karaoke synchronized lyrics view.
 * Features auto-scroll to active line, scale/glow emphasis, and tap-to-seek.
 */
@Composable
fun KaraokeLyricsView(
    lyrics: Lyrics?,
    activeLineIndex: Int,
    onSeekToLine: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (lyrics == null || lyrics.lines.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = if (lyrics?.plainLyrics?.isNotBlank() == true) lyrics.plainLyrics else "Lyrics not available for this track",
                color = VybeTextSecondary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 28.sp,
                modifier = Modifier.padding(32.dp)
            )
        }
        return
    }

    val listState = rememberLazyListState()

    // Smooth auto-scroll to center active line
    LaunchedEffect(activeLineIndex) {
        if (activeLineIndex in lyrics.lines.indices) {
            val targetIndex = (activeLineIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 120.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(lyrics.lines) { index, line ->
            val isActive = index == activeLineIndex

            val textColor by animateColorAsState(
                targetValue = if (isActive) Color.White else Color.White.copy(alpha = 0.28f),
                animationSpec = tween(400),
                label = "lyricTextColor"
            )

            val textScale by animateFloatAsState(
                targetValue = if (isActive) 1.06f else 0.98f,
                animationSpec = tween(400),
                label = "lyricScale"
            )

            Text(
                text = line.text,
                color = textColor,
                fontSize = if (isActive) 26.sp else 22.sp,
                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold,
                lineHeight = 36.sp,
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
