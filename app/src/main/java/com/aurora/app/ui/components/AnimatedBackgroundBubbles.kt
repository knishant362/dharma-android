package com.aurora.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedBackgroundBubbles() {
    val infiniteTransition = rememberInfiniteTransition()

    repeat(4) { index ->
        val animationOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 15000 + (index * 2000),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )

        val bubbleSize = (60 + index * 20).dp
        val startOffset = with(LocalDensity.current) {
            (-bubbleSize.toPx()).dp
        }
        val endOffset = with(LocalDensity.current) {
            (600.dp.toPx() + bubbleSize.toPx()).dp
        }

        Box(
            modifier = Modifier
                .size(bubbleSize)
                .offset(
                    x = (10 + index * 20).dp,
                    y = startOffset + (endOffset - startOffset) * animationOffset
                )
                .background(
                    Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
                .blur(5.dp)
        )
    }
}