package com.aurora.app.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Orange,
    onPrimary = DarkBlue,
    secondary = Orange,
    onSecondary = DarkBlue,
    background = DarkSurface,
    onBackground = White,
    surface = DarkSurface,
    onSurface = White
)

@Composable
fun AuroraTemplateTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = appTypography
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}