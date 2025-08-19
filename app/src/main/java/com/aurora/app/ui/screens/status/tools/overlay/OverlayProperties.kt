package com.aurora.app.ui.screens.status.tools.overlay

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import android.graphics.Color

@Parcelize
data class OverlayProperties(
    val bottomBackground: BottomBackgroundStyle = BottomBackgroundStyle(),
    val userProfile: UserProfileStyle = UserProfileStyle(),
    val topWatermark: TopWatermarkStyle = TopWatermarkStyle()
): Parcelable
@Parcelize
data class BottomBackgroundStyle(
    val type: BackgroundType = BackgroundType.GRADIENT,
    val gradientHeight: Float = 200f,
    val startColor: Int = Color.TRANSPARENT,
    val endColor: Int = Color.argb(220, 0, 0, 0),
    val solidColor: Int = Color.argb(180, 0, 0, 0),
    val cornerRadius: Float = 0f,
    val marginBottom: Float = 0f,
    val marginLeft: Float = 0f,
    val marginRight: Float = 0f
): Parcelable

@Parcelize
data class UserProfileStyle(
    val nameStyle: TextStyle = TextStyle(
        textSize = 36f,
        color = Color.WHITE,
        typeface = TextStyle.FontWeight.BOLD,
        positionX = 0.05f, // 5% from left
        positionY = 0.78f  // 78% from top
    ),
    val businessNameStyle: TextStyle = TextStyle(
        textSize = 28f,
        color = Color.argb(240, 255, 255, 255),
        typeface = TextStyle.FontWeight.NORMAL,
        positionX = 0.05f, // 5% from left
        positionY = 0.85f  // 85% from top
    ),
    val addressStyle: TextStyle = TextStyle(
        textSize = 24f,
        color = Color.argb(200, 255, 255, 255),
        typeface = TextStyle.FontWeight.NORMAL,
        positionX = 0.05f, // 5% from left
        positionY = 0.92f, // 92% from top
        maxWordsPerLine = 4,
        lineSpacing = 30f
    ),
    val profileCircle: ProfileCircleStyle = ProfileCircleStyle()
) : Parcelable

// Updated TextStyle with normalized position coordinates (0.0 to 1.0)
@Parcelize
data class TextStyle(
    val textSize: Float = 24f,
    val color: Int = Color.WHITE,
    val typeface: FontWeight = FontWeight.NORMAL,
    val positionX: Float = 0.05f, // 0.0 = left edge, 1.0 = right edge
    val positionY: Float = 0.85f, // 0.0 = top edge, 1.0 = bottom edge
    val maxWordsPerLine: Int = Int.MAX_VALUE,
    val lineSpacing: Float = 20f,
    val alignment: TextAlignment = TextAlignment.LEFT,
    val shadowColor: Int = Color.TRANSPARENT,
    val shadowRadius: Float = 0f,
    val shadowOffsetX: Float = 0f,
    val shadowOffsetY: Float = 0f
) : Parcelable {
    enum class FontWeight {
        NORMAL, BOLD, LIGHT
    }

    enum class TextAlignment {
        LEFT, CENTER, RIGHT
    }
}

// Updated ProfileCircleStyle with normalized position coordinates
@Parcelize
data class ProfileCircleStyle(
    val radius: Float = 40f,
    val positionX: Float = 0.9f, // 0.0 = left edge, 1.0 = right edge (90% = right side)
    val positionY: Float = 0.8f, // 0.0 = top edge, 1.0 = bottom edge (80% from top)
    val borderWidth: Float = 3f,
    val borderColor: Int = Color.WHITE,
    val gradientStartColor: Int = Color.argb(150, 255, 255, 255),
    val gradientEndColor: Int = Color.argb(80, 255, 255, 255),
    val textColor: Int = Color.WHITE,
    val textSize: Float = 32f,
    val isVisible: Boolean = true
): Parcelable

@Parcelize
data class TopWatermarkStyle(
    val height: Float = 90f,
    val backgroundColor: Int = Color.argb(255, 0, 0, 0),
    val textColor: Int = Color.WHITE,
    val textSize: Float = 22f,
    val lineColor: Int = Color.argb(255, 100, 149, 237),
    val lineWidth: Float = 3f,
    val lineLength: Float = 40f,
    val lineSpacing: Float = 20f,
    val text: String = "Made with Sanatan Dharma",
    val isVisible: Boolean = true
) : Parcelable

// Background type enumeration
@Parcelize
enum class BackgroundType : Parcelable {
    GRADIENT, SOLID, TRANSPARENT
}

// Extension function to convert FontWeight to Typeface
fun TextStyle.FontWeight.toTypeface(): Typeface {
    return when (this) {
        TextStyle.FontWeight.BOLD -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        TextStyle.FontWeight.LIGHT -> Typeface.create(Typeface.DEFAULT, Typeface.NORMAL) // Can be customized
        TextStyle.FontWeight.NORMAL -> Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }
}

// Extension function to convert TextAlignment to Paint.Align
fun TextStyle.TextAlignment.toPaintAlign(): Paint.Align {
    return when (this) {
        TextStyle.TextAlignment.LEFT -> Paint.Align.LEFT
        TextStyle.TextAlignment.CENTER -> Paint.Align.CENTER
        TextStyle.TextAlignment.RIGHT -> Paint.Align.RIGHT
    }
}