package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CineRedPrimary,
    onPrimary = Color.White,
    primaryContainer = CineRedContainer,
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = CineGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3E3000),
    onSecondaryContainer = CineGoldLight,
    tertiary = CineInfo,
    background = CineDarkBackground,
    onBackground = CineTextPrimary,
    surface = CineDarkSurface,
    onSurface = CineTextPrimary,
    surfaceVariant = CineDarkSurfaceElevated,
    onSurfaceVariant = CineTextSecondary,
    outline = CineBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Cinema streaming app always looks best in dark mode
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
