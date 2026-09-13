package com.alphabotz.vybemusic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.alphabotz.vybemusic.ui.theme.VybeBackground

@Composable
fun DynamicMeshBackground(
    dominantColor: Color = Color(0xFF8B5CF6),
    accentColor: Color = Color(0xFFEC4899),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val animatedDominant by animateColorAsState(
        targetValue = dominantColor,
        animationSpec = tween(1000),
        label = "dominantColor"
    )

    val animatedAccent by animateColorAsState(
        targetValue = accentColor,
        animationSpec = tween(1000),
        label = "accentColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "liquidMovement")
    val pulseOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 120f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseOffset"
    )

    val topGlow = Brush.radialGradient(
        colors = listOf(
            animatedDominant.copy(alpha = 0.40f),
            animatedAccent.copy(alpha = 0.15f),
            Color.Transparent
        ),
        center = Offset(200f + pulseOffset, 300f - pulseOffset * 0.5f),
        radius = 1100f
    )

    val bottomGlow = Brush.radialGradient(
        colors = listOf(
            animatedAccent.copy(alpha = 0.30f),
            animatedDominant.copy(alpha = 0.10f),
            Color.Transparent
        ),
        center = Offset(800f - pulseOffset, 1400f + pulseOffset * 0.5f),
        radius = 1200f
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VybeBackground)
            .background(topGlow)
            .background(bottomGlow)
    ) {
        content()
    }
}
