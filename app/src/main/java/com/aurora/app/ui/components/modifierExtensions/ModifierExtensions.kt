package com.aurora.app.ui.components.modifierExtensions

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.radialGradientBackground(
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.surface
    ),
    radius: Float = 600f,
    shape: Shape = RoundedCornerShape(16.dp),
    center: Offset = Offset.Infinite
): Modifier = this
    .background(
        brush = Brush.radialGradient(
            colors = colors,
            center = center,
            radius = radius
        ),
        shape = shape
    )