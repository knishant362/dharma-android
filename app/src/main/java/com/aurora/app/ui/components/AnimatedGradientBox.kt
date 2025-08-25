package com.aurora.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


@Composable
fun AnimatedGradientBox(
    currentStep: Int,
    content: @Composable BoxScope.() -> Unit
) {
    val currentGradient = stepGradients[currentStep.coerceIn(stepGradients.indices)]

    val animatedColor1 by animateColorAsState(
        targetValue = currentGradient[0],
        animationSpec = tween(durationMillis = 800),
        label = "gradient_color_1"
    )

    val animatedColor2 by animateColorAsState(
        targetValue = currentGradient[1],
        animationSpec = tween(durationMillis = 800),
        label = "gradient_color_2"
    )

    val animatedColor3 by animateColorAsState(
        targetValue = currentGradient[2],
        animationSpec = tween(durationMillis = 800),
        label = "gradient_color_3"
    )

    Box(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        animatedColor1,
                        animatedColor2,
                        animatedColor3
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        content()
    }
}

val stepGradients = listOf(
    listOf(
        Color(0xFFFF6B35), // Orange
        Color(0xFFFF8E53), // Light Orange
        Color(0xFFFFB347)  // Peach
    ),
    listOf(
        Color(0xFF667EEA), // Blue
        Color(0xFF764BA2), // Purple
        Color(0xFF8E44AD)  // Dark Purple
    ),
    listOf(
        Color(0xFF11998E), // Teal
        Color(0xFF38EF7D), // Light Green
        Color(0xFF2ECC71)  // Green
    ),
    listOf(
        Color(0xFFAA076B), // Deep Pink
        Color(0xFFCC2B5E), // Pink
        Color(0xFFE94057)  // Red Pink
    ),
    listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFFFFA500), // Orange
        Color(0xFFFF8C00)  // Dark Orange
    )
)