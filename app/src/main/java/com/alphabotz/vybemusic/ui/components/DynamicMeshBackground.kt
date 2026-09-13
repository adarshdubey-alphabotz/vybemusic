package com.alphabotz.vybemusic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.alphabotz.vybemusic.ui.theme.VybeBackground

@Composable
fun DynamicMeshBackground(
    dominantColor: Color = Color(0xFF7000FF),
    accentColor: Color = Color(0xFFFF007F),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val animatedDominant by animateColorAsState(
        targetValue = dominantColor,
        animationSpec = tween(1200),
        label = "dominantColor"
    )

    val animatedAccent by animateColorAsState(
        targetValue = accentColor,
        animationSpec = tween(1200),
        label = "accentColor"
    )

    val gradient = Brush.radialGradient(
        colors = listOf(
            animatedDominant.copy(alpha = 0.35f),
            animatedAccent.copy(alpha = 0.20f),
            VybeBackground.copy(alpha = 0.95f),
            VybeBackground
        ),
        radius = 1800f
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VybeBackground)
            .background(gradient)
    ) {
        content()
    }
}
