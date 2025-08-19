package com.aurora.app.ui.screens.status.tools.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurora.app.ui.screens.status.model.UserProfile

@Composable
fun OverlayPreview(
    scale: Float,
    overlayProperties: OverlayProperties,
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val width = maxWidth
        val height = maxHeight

        // Calculate actual pixel positions from normalized coordinates
        val nameStyle = overlayProperties.userProfile.nameStyle
        val businessStyle = overlayProperties.userProfile.businessNameStyle
        val addressStyle = overlayProperties.userProfile.addressStyle
        val circleStyle = overlayProperties.userProfile.profileCircle

        // Top Watermark
        if (overlayProperties.topWatermark.isVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((overlayProperties.topWatermark.height * scale).dp)
                    .background(Color(overlayProperties.topWatermark.backgroundColor)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(
                        modifier = Modifier.width((overlayProperties.topWatermark.lineLength * scale).dp),
                        thickness = (overlayProperties.topWatermark.lineWidth * scale).dp,
                        color = Color(overlayProperties.topWatermark.lineColor)
                    )
                    Text(
                        text = overlayProperties.topWatermark.text,
                        color = Color(overlayProperties.topWatermark.textColor),
                        fontSize = (overlayProperties.topWatermark.textSize * scale).sp,
                        modifier = Modifier.padding(horizontal = (overlayProperties.topWatermark.lineSpacing * scale).dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.width((overlayProperties.topWatermark.lineLength * scale).dp),
                        thickness = (overlayProperties.topWatermark.lineWidth * scale).dp,
                        color = Color(overlayProperties.topWatermark.lineColor)
                    )
                }
            }
        }

        // Bottom Background with Dynamic Height
        val bottomStyle = overlayProperties.bottomBackground

        // Calculate required background height based on content positions
        val namePosition = nameStyle.positionY
        val businessPosition = businessStyle.positionY
        val addressPosition = addressStyle.positionY
        val circlePosition = if (circleStyle.isVisible) circleStyle.positionY else 0f

        // Find the topmost content position
        val topMostPosition = minOf(namePosition, businessPosition, addressPosition, circlePosition)

        // Calculate dynamic height as percentage of total height
        val backgroundHeightPercent = 1f - topMostPosition + 0.05f // Add 5% padding
        val backgroundHeight = height * backgroundHeightPercent

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(backgroundHeight)
                .let { mod ->
                    if (bottomStyle.cornerRadius > 0) {
                        mod
                            .padding(
                                horizontal = (bottomStyle.marginLeft + bottomStyle.marginRight).dp / 2,
                                vertical = (bottomStyle.marginBottom).dp
                            )
                            .clip(RoundedCornerShape((bottomStyle.cornerRadius * scale).dp))
                    } else mod
                }
                .background(
                    when (bottomStyle.type) {
                        BackgroundType.GRADIENT -> Brush.verticalGradient(
                            colors = listOf(Color(bottomStyle.startColor), Color(bottomStyle.endColor))
                        )
                        BackgroundType.SOLID -> Brush.verticalGradient(
                            colors = listOf(Color(bottomStyle.solidColor), Color(bottomStyle.solidColor))
                        )
                        BackgroundType.TRANSPARENT -> Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Transparent)
                        )
                    }
                )
        ) {
            // User Name Text - positioned using normalized coordinates
            Text(
                text = userProfile.name,
                color = Color(nameStyle.color),
                fontSize = (nameStyle.textSize * scale * 0.5f).sp, // Scale down for preview
                fontWeight = if (nameStyle.typeface == TextStyle.FontWeight.BOLD) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .offset(
                        x = (width * nameStyle.positionX) - (width * if (bottomStyle.cornerRadius > 0) (bottomStyle.marginLeft * scale / width.value) else 0f),
                        y = (height * nameStyle.positionY) - (height * (1f - backgroundHeightPercent))
                    )
            )

            // Business Name Text
            Text(
                text = userProfile.businessName,
                color = Color(businessStyle.color),
                fontSize = (businessStyle.textSize * scale * 0.5f).sp, // Scale down for preview
                fontWeight = if (businessStyle.typeface == TextStyle.FontWeight.BOLD) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .offset(
                        x = (width * businessStyle.positionX) - (width * if (bottomStyle.cornerRadius > 0) (bottomStyle.marginLeft * scale / width.value) else 0f),
                        y = (height * businessStyle.positionY) - (height * (1f - backgroundHeightPercent))
                    )
            )

            // Address Text
            val addressWords = userProfile.address.split(" ")
            val addressLine1 = if (addressWords.size > addressStyle.maxWordsPerLine) {
                addressWords.take(addressStyle.maxWordsPerLine).joinToString(" ")
            } else {
                userProfile.address
            }

            Text(
                text = addressLine1,
                color = Color(addressStyle.color),
                fontSize = (addressStyle.textSize * scale * 0.5f).sp, // Scale down for preview
                fontWeight = if (addressStyle.typeface == TextStyle.FontWeight.BOLD) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .offset(
                        x = (width * addressStyle.positionX) - (width * if (bottomStyle.cornerRadius > 0) (bottomStyle.marginLeft * scale / width.value) else 0f),
                        y = (height * addressStyle.positionY) - (height * (1f - backgroundHeightPercent))
                    )
            )

            // Address Line 2 (if needed)
            if (addressWords.size > addressStyle.maxWordsPerLine) {
                val addressLine2 = addressWords.drop(addressStyle.maxWordsPerLine).joinToString(" ")
                Text(
                    text = addressLine2,
                    color = Color(addressStyle.color),
                    fontSize = (addressStyle.textSize * scale * 0.5f).sp,
                    fontWeight = if (addressStyle.typeface == TextStyle.FontWeight.BOLD) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .offset(
                            x = (width * addressStyle.positionX) - (width * if (bottomStyle.cornerRadius > 0) (bottomStyle.marginLeft * scale / width.value) else 0f),
                            y = (height * addressStyle.positionY) - (height * (1f - backgroundHeightPercent)) + (addressStyle.lineSpacing * scale * 0.5f).dp
                        )
                )
            }

            // Profile Circle - positioned using normalized coordinates
            if (circleStyle.isVisible) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (width * circleStyle.positionX) - (circleStyle.radius * scale * 0.5f).dp - (width * if (bottomStyle.cornerRadius > 0) (bottomStyle.marginRight * scale / width.value) else 0f),
                            y = (height * circleStyle.positionY) - (height * (1f - backgroundHeightPercent)) - (circleStyle.radius * scale * 0.5f).dp
                        )
                        .size((circleStyle.radius * scale).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(circleStyle.gradientStartColor),
                                    Color(circleStyle.gradientEndColor)
                                )
                            )
                        )
                        .border(
                            (circleStyle.borderWidth * scale * 0.5f).dp,
                            Color(circleStyle.borderColor),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                        color = Color(circleStyle.textColor),
                        fontSize = (circleStyle.textSize * scale * 0.5f).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}