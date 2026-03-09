package com.example.musicshelf.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MusicShelfDarkColorScheme = darkColorScheme(
    primary = MusicPrimary,
    onPrimary = MusicOnPrimary,
    secondary = MusicSecondary,
    onSecondary = MusicOnPrimary,
    error = MusicError,
    onError = MusicOnPrimary,
    background = MusicBackground,
    onBackground = MusicOnSurface,
    surface = MusicSurface,
    onSurface = MusicOnSurface,
    surfaceVariant = MusicSurfaceVariant,
    onSurfaceVariant = MusicTextSecondary,
    outline = MusicOutline
)

private val MusicShelfLightColorScheme = lightColorScheme(
    primary = MusicPrimary,
    onPrimary = Color.White,
    secondary = MusicSecondary,
    onSecondary = Color.White,
    error = MusicError,
    onError = Color.White,
    background = Color(0xFFF5F5FA),
    onBackground = Color(0xFF101015),
    surface = Color.White,
    onSurface = Color(0xFF101015),
    surfaceVariant = Color(0xFFE5E5EA),
    onSurfaceVariant = Color(0xFF5A5A65),
    outline = Color(0xFFD0D0D5)
)

@Composable
fun MusicShelfTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MusicShelfDarkColorScheme else MusicShelfLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MusicShelfTypography,
        content = content
    )
}