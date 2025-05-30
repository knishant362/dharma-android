package com.aurora.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun LinearProgressWithIcon(
    progress: Float,
    modifier: Modifier = Modifier,
    barHeight: Dp = 12.dp,
    iconSize: Dp = 24.dp
) {
    val gradient = Brush.horizontalGradient(
        listOf(Color.Magenta, Color.Cyan, Color.Blue)
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )

    val density = LocalDensity.current
    val barWidth = 300.dp

    Box(
        modifier = modifier
            .width(barWidth)
            .height(barHeight + iconSize / 2)
    ) {
        // Background bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .clip(RoundedCornerShape(50))
                .background(Color.LightGray.copy(alpha = 0.3f))
                .align(Alignment.CenterStart)
        )

        // Gradient fill
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(barHeight)
                .clip(RoundedCornerShape(50))
                .background(gradient)
                .align(Alignment.CenterStart)
        )

        // Moving icon
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Progress Icon",
            modifier = Modifier
                .size(iconSize)
                .offset {
                    val pxOffset = with(density) {
                        ((barWidth.toPx() - iconSize.toPx()) * animatedProgress).toInt()
                    }
                    IntOffset(x = pxOffset, y = 0)
                }
                .align(Alignment.CenterStart),
            tint = Color.White
        )
    }
}
