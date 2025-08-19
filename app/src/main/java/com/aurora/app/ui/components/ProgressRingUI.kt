package com.aurora.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurora.app.designsystem.theme.Horoscope2
import com.aurora.app.designsystem.theme.Purple1
import com.aurora.app.designsystem.theme.Purple3
import kotlin.math.roundToInt


@Preview
@Composable
fun ProgressRingUIPreview(modifier: Modifier = Modifier) {
    ProgressRingUI(
        icCompleted = true,
        progress = 100f,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color.White)
    )
}

@Composable
fun ProgressRingUI(
    icCompleted: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .padding(24.dp)
            .size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = 0.2f),
                            Color(0xFF8B5CF6).copy(alpha = 0.2f),
                            Color(0xFFEC4899).copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        radius = 200f
                    ),
                    CircleShape
                )
        )

        Canvas(
            modifier = Modifier.size(160.dp)
        ) {
            val strokeWidth = 10.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background circle
            drawCircle(
                color = Color(0xFFE5E7EB),
                radius = radius,
                center = center,
                style = Stroke(strokeWidth * 0.8f, cap = StrokeCap.Round)
            )

            // Progress arc with glow effect
            val sweepAngle = (progress / 100f) * 360f
            drawArc(
                brush = Brush.sweepGradient(colors = listOf(Purple1, Horoscope2, Purple3)),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(size.width - strokeWidth, size.height - strokeWidth)
            )
        }

        if (progress >= 100f && icCompleted) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.animateContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${progress.roundToInt()}%",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Creating...",
                    fontSize = 16.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}