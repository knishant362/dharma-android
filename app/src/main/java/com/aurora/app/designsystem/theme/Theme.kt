package com.aurora.app.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Golden,
    onPrimary = DarkBlue,
    secondary = Golden,
    onSecondary = DarkBlue,
    background = DarkBlue,
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
        typography = appTypography,
        content = content
    )
}