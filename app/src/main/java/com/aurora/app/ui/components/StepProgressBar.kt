package com.aurora.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StepProgressBar(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 16.dp,
    cornerRadius: Dp = 50.dp,
    stepDotSize: Dp = 8.dp,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.secondary
    ),
    trackBackgroundColor: Color = MaterialTheme.colorScheme.onPrimary,
    horizontalPadding: Dp = 24.dp // add horizontal padding parameter
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding), // apply padding here
    ) {
        val totalWidth = constraints.maxWidth.toFloat()
        val spacingPx = totalWidth / (totalSteps - 1)
        val animatedOffsetX by animateDpAsState(
            targetValue = with(LocalDensity.current) { (spacingPx * currentStep).toDp() },
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            label = "star_offset"
        )
        val animatedProgressWidth by animateDpAsState(
            targetValue = with(LocalDensity.current) { (spacingPx * currentStep).toDp() },
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            label = "progress_width"
        )

        // 1. Background and fill bar (clipped)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(cornerRadius))
                .background(trackBackgroundColor)
        )

        // 2. Progress Fill
        Box(
            modifier = Modifier
                .width(animatedProgressWidth)
                .height(trackHeight)
                .clip(RoundedCornerShape(cornerRadius))
                .background(brush = Brush.horizontalGradient(gradientColors))
        )

        // 3. Step Dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSteps) {
                Icon(
                    imageVector = Icons.Rounded.Circle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(stepDotSize)
                )
            }
        }

        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(34.dp)
                .offset(x = animatedOffsetX - 17.dp, y = -(trackHeight / 2)) // center + elevate
                .align(Alignment.TopStart)
        )
    }
}

@Preview
@Composable
fun StepProgressPreview(modifier: Modifier = Modifier) {
    StepProgressBar(
        totalSteps = 6,
        currentStep = 2,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
