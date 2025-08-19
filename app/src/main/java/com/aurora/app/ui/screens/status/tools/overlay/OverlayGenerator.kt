package com.aurora.app.ui.screens.status.tools.overlay

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import com.aurora.app.ui.screens.status.model.UserProfile

fun drawScaledOverlay(
    canvas: Canvas,
    userProfile: UserProfile,
    videoWidth: Int,
    videoHeight: Int,
    overlayProperties: OverlayProperties = OverlayProperties()
) {
    val scale = videoWidth / 720f
    val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.LEFT
    }

    // Calculate required background height based on text content
    val requiredHeight = calculateRequiredBackgroundHeight(
        canvas, paint, userProfile, scale, overlayProperties.userProfile
    )

    // Draw components based on properties
    if (overlayProperties.topWatermark.isVisible) {
        drawTopWatermark(canvas, paint, scale, videoWidth, overlayProperties.topWatermark)
    }

    drawBottomGradient(canvas, paint, videoHeight, scale, overlayProperties.bottomBackground, requiredHeight)
    drawUserProfile(canvas, paint, userProfile, scale, overlayProperties.userProfile)
}

// Updated function to calculate required background height with normalized positions
private fun calculateRequiredBackgroundHeight(
    canvas: Canvas,
    paint: Paint,
    userProfile: UserProfile,
    scale: Float,
    style: UserProfileStyle
): Float {
    // Get Y positions in actual pixels using normalized coordinates
    val nameY = style.nameStyle.positionY * canvas.height
    val businessY = style.businessNameStyle.positionY * canvas.height
    val addressY = style.addressStyle.positionY * canvas.height

    // Calculate address lines
    val addressWords = userProfile.address.split(" ")
    val addressLines = if (addressWords.size > style.addressStyle.maxWordsPerLine) 2 else 1
    val addressBottomY = addressY + ((addressLines - 1) * style.addressStyle.lineSpacing * scale)

    // Profile circle position (if visible)
    val circleBottomY = if (style.profileCircle.isVisible) {
        (style.profileCircle.positionY * canvas.height) + (style.profileCircle.radius * scale)
    } else 0f

    // Find the bottommost position
    val bottomMostY = maxOf(nameY, businessY, addressBottomY, circleBottomY)

    // Add padding for better visual appearance
    val padding = 30f * scale

    // Calculate total required height from the topmost content
    val topMostY = minOf(nameY, businessY, addressY) - (50f * scale) // 50f for text height estimation

    return bottomMostY - topMostY + padding
}

// Updated bottom gradient function with dynamic height
private fun drawBottomGradient(
    canvas: Canvas,
    paint: Paint,
    videoHeight: Int,
    scale: Float,
    style: BottomBackgroundStyle,
    dynamicHeight: Float
) {
    when (style.type) {
        BackgroundType.GRADIENT -> {
            // Use dynamic height instead of static gradientHeight
            val gradientHeight = dynamicHeight
            val gradientPaint = Paint().apply {
                shader = LinearGradient(
                    0f, canvas.height - gradientHeight,
                    0f, canvas.height.toFloat(),
                    style.startColor,
                    style.endColor,
                    Shader.TileMode.CLAMP
                )
            }

            if (style.cornerRadius > 0f) {
                canvas.drawRoundRect(
                    style.marginLeft * scale,
                    canvas.height - gradientHeight,
                    canvas.width - (style.marginRight * scale),
                    canvas.height - (style.marginBottom * scale),
                    style.cornerRadius * scale,
                    style.cornerRadius * scale,
                    gradientPaint
                )
            } else {
                canvas.drawRect(
                    style.marginLeft * scale,
                    canvas.height - gradientHeight,
                    canvas.width - (style.marginRight * scale),
                    canvas.height - (style.marginBottom * scale),
                    gradientPaint
                )
            }
        }
        BackgroundType.SOLID -> {
            paint.shader = null
            paint.color = style.solidColor

            // Use dynamic height instead of static gradientHeight
            val backgroundHeight = dynamicHeight

            if (style.cornerRadius > 0f) {
                canvas.drawRoundRect(
                    style.marginLeft * scale,
                    canvas.height - backgroundHeight,
                    canvas.width - (style.marginRight * scale),
                    canvas.height - (style.marginBottom * scale),
                    style.cornerRadius * scale,
                    style.cornerRadius * scale,
                    paint
                )
            } else {
                canvas.drawRect(
                    style.marginLeft * scale,
                    canvas.height - backgroundHeight,
                    canvas.width - (style.marginRight * scale),
                    canvas.height - (style.marginBottom * scale),
                    paint
                )
            }
        }
        BackgroundType.TRANSPARENT -> {
            // No background drawn
        }
    }
}

// Updated top watermark function
private fun drawTopWatermark(
    canvas: Canvas,
    paint: Paint,
    scale: Float,
    width: Int,
    style: TopWatermarkStyle
) {
    val left = 0f
    val top = 0f
    val right = width.toFloat()
    val bottom = style.height * scale

    // Background
    paint.color = style.backgroundColor
    canvas.drawRect(left, top, right, bottom, paint)

    // Text
    val centerX = width / 2f
    val textY = (bottom / 2f) + (8f * scale)

    paint.color = style.textColor
    paint.textSize = style.textSize * scale
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textAlign = Paint.Align.CENTER

    canvas.drawText(style.text, centerX, textY, paint)

    // Accent lines
    paint.color = style.lineColor
    paint.strokeWidth = style.lineWidth * scale

    val textWidth = paint.measureText(style.text)
    val lineLength = style.lineLength * scale
    val lineY = textY - (8f * scale)
    val spacing = style.lineSpacing * scale

    // Left line
    val leftLineStart = centerX - (textWidth / 2f) - spacing - lineLength
    val leftLineEnd = centerX - (textWidth / 2f) - spacing
    canvas.drawLine(leftLineStart, lineY, leftLineEnd, lineY, paint)

    // Right line
    val rightLineStart = centerX + (textWidth / 2f) + spacing
    val rightLineEnd = centerX + (textWidth / 2f) + spacing + lineLength
    canvas.drawLine(rightLineStart, lineY, rightLineEnd, lineY, paint)

    paint.textAlign = Paint.Align.LEFT
}

// Updated user profile function
private fun drawUserProfile(
    canvas: Canvas,
    paint: Paint,
    userProfile: UserProfile,
    scale: Float,
    style: UserProfileStyle
) {
    paint.shader = null

    // Draw user name
    drawStyledText(
        canvas, paint, userProfile.name,
        style.nameStyle, scale
    )

    // Draw business name
    drawStyledText(
        canvas, paint, userProfile.businessName,
        style.businessNameStyle, scale
    )

    // Draw address with line wrapping
    drawStyledAddress(
        canvas, paint, userProfile.address,
        style.addressStyle, scale
    )

    // Draw profile circle
    if (style.profileCircle.isVisible) {
        drawProfileCircle(canvas, paint, userProfile, scale, style.profileCircle)
    }
}

// Updated helper function to draw styled text with normalized coordinates
private fun drawStyledText(
    canvas: Canvas,
    paint: Paint,
    text: String,
    textStyle: TextStyle,
    scale: Float
) {
    paint.color = textStyle.color
    paint.textSize = textStyle.textSize * scale
    paint.typeface = textStyle.typeface.toTypeface()
    paint.textAlign = textStyle.alignment.toPaintAlign()

    // Apply shadow if specified
    if (textStyle.shadowColor != Color.TRANSPARENT) {
        paint.setShadowLayer(
            textStyle.shadowRadius * scale,
            textStyle.shadowOffsetX * scale,
            textStyle.shadowOffsetY * scale,
            textStyle.shadowColor
        )
    }

    // Calculate position using normalized coordinates (0.0 to 1.0)
    val x = when (textStyle.alignment) {
        TextStyle.TextAlignment.LEFT -> textStyle.positionX * canvas.width
        TextStyle.TextAlignment.CENTER -> {
            val centerX = textStyle.positionX * canvas.width
            paint.textAlign = Paint.Align.CENTER
            centerX
        }
        TextStyle.TextAlignment.RIGHT -> {
            val rightX = textStyle.positionX * canvas.width
            paint.textAlign = Paint.Align.RIGHT
            rightX
        }
    }

    val y = textStyle.positionY * canvas.height

    canvas.drawText(text, x, y, paint)

    // Clear shadow
    paint.clearShadowLayer()
    // Reset text alignment
    paint.textAlign = Paint.Align.LEFT
}

// Updated address drawing function with normalized coordinates
private fun drawStyledAddress(
    canvas: Canvas,
    paint: Paint,
    address: String,
    textStyle: TextStyle,
    scale: Float
) {
    paint.color = textStyle.color
    paint.textSize = textStyle.textSize * scale
    paint.typeface = textStyle.typeface.toTypeface()
    paint.textAlign = textStyle.alignment.toPaintAlign()

    val addressWords = address.split(" ")
    val x = when (textStyle.alignment) {
        TextStyle.TextAlignment.LEFT -> textStyle.positionX * canvas.width
        TextStyle.TextAlignment.CENTER -> {
            paint.textAlign = Paint.Align.CENTER
            textStyle.positionX * canvas.width
        }
        TextStyle.TextAlignment.RIGHT -> {
            paint.textAlign = Paint.Align.RIGHT
            textStyle.positionX * canvas.width
        }
    }

    val baseY = textStyle.positionY * canvas.height

    if (addressWords.size > textStyle.maxWordsPerLine) {
        val line1 = addressWords.take(textStyle.maxWordsPerLine).joinToString(" ")
        val line2 = addressWords.drop(textStyle.maxWordsPerLine).joinToString(" ")

        canvas.drawText(line1, x, baseY, paint)
        canvas.drawText(line2, x, baseY + (textStyle.lineSpacing * scale), paint)
    } else {
        canvas.drawText(address, x, baseY, paint)
    }

    // Reset text alignment
    paint.textAlign = Paint.Align.LEFT
}

// Updated profile circle function with normalized coordinates
private fun drawProfileCircle(
    canvas: Canvas,
    paint: Paint,
    userProfile: UserProfile,
    scale: Float,
    style: ProfileCircleStyle
) {
    val centerX = style.positionX * canvas.width
    val centerY = style.positionY * canvas.height
    val radius = style.radius * scale

    // Circle gradient background
    val circlePaint = Paint().apply {
        isAntiAlias = true
        shader = RadialGradient(
            centerX, centerY, radius,
            style.gradientStartColor,
            style.gradientEndColor,
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawCircle(centerX, centerY, radius, circlePaint)

    // Circle border
    paint.shader = null
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = style.borderWidth * scale
    paint.color = style.borderColor
    canvas.drawCircle(centerX, centerY, radius, paint)

    // Profile initial
    paint.style = Paint.Style.FILL
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = style.textSize * scale
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = style.textColor
    canvas.drawText(
        userProfile.name.firstOrNull()?.uppercase() ?: "U",
        centerX,
        centerY + (10f * scale),
        paint
    )

    // Reset paint properties
    paint.style = Paint.Style.FILL
    paint.textAlign = Paint.Align.LEFT
}