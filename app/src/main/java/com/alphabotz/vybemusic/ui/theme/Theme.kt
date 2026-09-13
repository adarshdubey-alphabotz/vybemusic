package com.alphabotz.vybemusic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VybePrimary,
    secondary = VybeAccent,
    tertiary = VybeCyan,
    background = VybeBackground,
    surface = VybeSurface,
    surfaceVariant = VybeSurfaceElevated,
    onPrimary = VybeTextPrimary,
    onSecondary = VybeTextPrimary,
    onBackground = VybeTextPrimary,
    onSurface = VybeTextPrimary,
    onSurfaceVariant = VybeTextSecondary
)

@Composable
fun VybeMusicTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
