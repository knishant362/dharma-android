package com.aurora.app.ui.screens.status.tools.overlay

import android.graphics.Color

object DefaultOverlayProperties {

    val OverlayPropertyOne = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.GRADIENT,
            startColor = Color.TRANSPARENT,
            endColor = Color.argb(220, 0, 0, 0),
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 36f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.BOLD,
                positionX = 0.05f, // 5% from left
                positionY = 0.85f, // 78% from top
                shadowColor = Color.argb(150, 0, 0, 0),
                shadowRadius = 4f,
                shadowOffsetX = 1f,
                shadowOffsetY = 1f
            ),
            businessNameStyle = TextStyle(
                textSize = 28f,
                color = Color.argb(240, 255, 255, 255),
                positionX = 0.05f, // 5% from left
                positionY = 0.89f, // 85% from top
                typeface = TextStyle.FontWeight.NORMAL
            ),
            addressStyle = TextStyle(
                textSize = 24f,
                color = Color.argb(200, 255, 255, 255),
                positionX = 0.05f, // 5% from left
                positionY = 0.93f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 30f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 40f,
                positionX = 0.9f, // 90% from left
                positionY = 0.9f, // 80% from top
                borderWidth = 3f,
                borderColor = Color.WHITE,
                gradientStartColor = Color.argb(150, 255, 255, 255),
                gradientEndColor = Color.argb(80, 255, 255, 255)
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 0, 0, 0),
            lineColor = Color.argb(255, 100, 149, 237),
            textColor = Color.WHITE
        )
    )

    // Design 2: Minimalist Orange Style
    val OverlayPropertyTwo = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.SOLID,
            solidColor = Color.argb(150, 0, 0, 0),
            cornerRadius = 20f,
            marginLeft = 20f,
            marginRight = 20f,
            marginBottom = 20f,
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 32f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.LIGHT,
                positionX = 0.08f, // 8% from left (accounting for card margins)
                positionY = 0.78f, // 78% from top
                shadowColor = Color.argb(100, 0, 0, 0),
                shadowRadius = 3f,
                shadowOffsetY = 1f
            ),
            businessNameStyle = TextStyle(
                textSize = 24f,
                color = Color.argb(255, 255, 165, 0), // Orange accent
                positionX = 0.08f, // 8% from left
                positionY = 0.85f, // 85% from top
                typeface = TextStyle.FontWeight.NORMAL
            ),
            addressStyle = TextStyle(
                textSize = 20f,
                color = Color.argb(220, 255, 255, 255),
                positionX = 0.08f, // 8% from left
                positionY = 0.92f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 28f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 35f,
                positionX = 0.88f, // 88% from left (accounting for margins)
                positionY = 0.8f, // 80% from top
                borderColor = Color.argb(255, 255, 165, 0),
                borderWidth = 2f
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 0, 0, 0),
            lineColor = Color.argb(255, 255, 165, 0),
            textColor = Color.WHITE
        )
    )

    // Design 3: Social Media Purple Style
    val OverlayPropertyThree = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.GRADIENT,
            startColor = Color.TRANSPARENT,
            endColor = Color.argb(200, 138, 43, 226), // Purple gradient
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 38f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.BOLD,
                positionX = 0.05f, // 5% from left
                positionY = 0.78f, // 78% from top
                shadowColor = Color.argb(150, 0, 0, 0),
                shadowRadius = 6f,
                shadowOffsetX = 2f,
                shadowOffsetY = 2f
            ),
            businessNameStyle = TextStyle(
                textSize = 26f,
                color = Color.argb(255, 255, 20, 147), // Deep pink
                positionX = 0.05f, // 5% from left
                positionY = 0.85f, // 85% from top
                typeface = TextStyle.FontWeight.BOLD
            ),
            addressStyle = TextStyle(
                textSize = 22f,
                color = Color.WHITE,
                positionX = 0.05f, // 5% from left
                positionY = 0.92f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 30f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 45f,
                positionX = 0.9f, // 90% from left
                positionY = 0.8f, // 80% from top
                borderWidth = 4f,
                borderColor = Color.argb(255, 255, 20, 147),
                gradientStartColor = Color.argb(180, 255, 255, 255),
                gradientEndColor = Color.argb(100, 255, 20, 147)
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 138, 43, 226),
            lineColor = Color.argb(255, 255, 20, 147),
            textColor = Color.WHITE
        )
    )

    // Design 4: Ocean Blue Theme
    val OverlayPropertyOcean = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.GRADIENT,
            startColor = Color.TRANSPARENT,
            endColor = Color.argb(220, 25, 25, 112), // Navy blue gradient
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 38f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.BOLD,
                positionX = 0.05f, // 5% from left
                positionY = 0.78f, // 78% from top
                shadowColor = Color.argb(150, 0, 0, 0),
                shadowRadius = 6f,
                shadowOffsetX = 2f,
                shadowOffsetY = 2f
            ),
            businessNameStyle = TextStyle(
                textSize = 26f,
                color = Color.argb(255, 0, 191, 255), // Deep sky blue
                positionX = 0.05f, // 5% from left
                positionY = 0.85f, // 85% from top
                typeface = TextStyle.FontWeight.BOLD
            ),
            addressStyle = TextStyle(
                textSize = 22f,
                color = Color.WHITE,
                positionX = 0.05f, // 5% from left
                positionY = 0.92f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 30f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 45f,
                positionX = 0.9f, // 90% from left
                positionY = 0.8f, // 80% from top
                borderWidth = 4f,
                borderColor = Color.argb(255, 0, 191, 255),
                gradientStartColor = Color.argb(180, 255, 255, 255),
                gradientEndColor = Color.argb(100, 0, 191, 255)
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 25, 25, 112),
            lineColor = Color.argb(255, 0, 191, 255),
            textColor = Color.WHITE
        )
    )

    // Design 5: Sunset Orange Theme
    val OverlayPropertySunset = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.GRADIENT,
            startColor = Color.TRANSPARENT,
            endColor = Color.argb(210, 255, 69, 0), // Orange red gradient
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 38f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.BOLD,
                positionX = 0.05f, // 5% from left
                positionY = 0.78f, // 78% from top
                shadowColor = Color.argb(150, 0, 0, 0),
                shadowRadius = 6f,
                shadowOffsetX = 2f,
                shadowOffsetY = 2f
            ),
            businessNameStyle = TextStyle(
                textSize = 26f,
                color = Color.argb(255, 255, 215, 0), // Gold
                positionX = 0.05f, // 5% from left
                positionY = 0.85f, // 85% from top
                typeface = TextStyle.FontWeight.BOLD
            ),
            addressStyle = TextStyle(
                textSize = 22f,
                color = Color.WHITE,
                positionX = 0.05f, // 5% from left
                positionY = 0.92f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 30f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 45f,
                positionX = 0.9f, // 90% from left
                positionY = 0.8f, // 80% from top
                borderWidth = 4f,
                borderColor = Color.argb(255, 255, 215, 0),
                gradientStartColor = Color.argb(180, 255, 255, 255),
                gradientEndColor = Color.argb(100, 255, 215, 0)
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 255, 69, 0),
            lineColor = Color.argb(255, 255, 215, 0),
            textColor = Color.WHITE
        )
    )

    // Design 6: Forest Green Theme
    val OverlayPropertyForest = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.SOLID,
            solidColor = Color.argb(190, 34, 139, 34), // Forest green solid
            cornerRadius = 15f,
            marginLeft = 15f,
            marginRight = 15f,
            marginBottom = 15f,
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 38f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.BOLD,
                positionX = 0.07f, // 7% from left (accounting for margins)
                positionY = 0.78f, // 78% from top
                shadowColor = Color.argb(150, 0, 0, 0),
                shadowRadius = 6f,
                shadowOffsetX = 2f,
                shadowOffsetY = 2f
            ),
            businessNameStyle = TextStyle(
                textSize = 26f,
                color = Color.argb(255, 144, 238, 144), // Light green
                positionX = 0.07f, // 7% from left
                positionY = 0.85f, // 85% from top
                typeface = TextStyle.FontWeight.BOLD
            ),
            addressStyle = TextStyle(
                textSize = 22f,
                color = Color.WHITE,
                positionX = 0.07f, // 7% from left
                positionY = 0.92f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 30f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 45f,
                positionX = 0.88f, // 88% from left (accounting for margins)
                positionY = 0.8f, // 80% from top
                borderWidth = 4f,
                borderColor = Color.argb(255, 144, 238, 144),
                gradientStartColor = Color.argb(180, 255, 255, 255),
                gradientEndColor = Color.argb(100, 144, 238, 144)
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 34, 139, 34),
            lineColor = Color.argb(255, 144, 238, 144),
            textColor = Color.WHITE
        )
    )

    // Design 7: Crimson Red Theme
    val OverlayPropertyCrimson = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.GRADIENT,
            startColor = Color.TRANSPARENT,
            endColor = Color.argb(215, 220, 20, 60), // Crimson gradient
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 38f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.BOLD,
                positionX = 0.05f, // 5% from left
                positionY = 0.78f, // 78% from top
                shadowColor = Color.argb(150, 0, 0, 0),
                shadowRadius = 6f,
                shadowOffsetX = 2f,
                shadowOffsetY = 2f
            ),
            businessNameStyle = TextStyle(
                textSize = 26f,
                color = Color.argb(255, 255, 182, 193), // Light pink
                positionX = 0.05f, // 5% from left
                positionY = 0.85f, // 85% from top
                typeface = TextStyle.FontWeight.BOLD
            ),
            addressStyle = TextStyle(
                textSize = 22f,
                color = Color.WHITE,
                positionX = 0.05f, // 5% from left
                positionY = 0.92f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 30f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 45f,
                positionX = 0.9f, // 90% from left
                positionY = 0.8f, // 80% from top
                borderWidth = 4f,
                borderColor = Color.argb(255, 255, 182, 193),
                gradientStartColor = Color.argb(180, 255, 255, 255),
                gradientEndColor = Color.argb(100, 255, 182, 193)
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 220, 20, 60),
            lineColor = Color.argb(255, 255, 182, 193),
            textColor = Color.WHITE
        )
    )

    // Design 8: Royal Purple Theme
    val OverlayPropertyRoyal = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.SOLID,
            solidColor = Color.argb(200, 75, 0, 130), // Indigo solid
            cornerRadius = 20f,
            marginLeft = 20f,
            marginRight = 20f,
            marginBottom = 20f,
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 38f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.BOLD,
                positionX = 0.08f, // 8% from left (accounting for margins)
                positionY = 0.78f, // 78% from top
                shadowColor = Color.argb(150, 0, 0, 0),
                shadowRadius = 6f,
                shadowOffsetX = 2f,
                shadowOffsetY = 2f
            ),
            businessNameStyle = TextStyle(
                textSize = 26f,
                color = Color.argb(255, 186, 85, 211), // Medium orchid
                positionX = 0.08f, // 8% from left
                positionY = 0.85f, // 85% from top
                typeface = TextStyle.FontWeight.BOLD
            ),
            addressStyle = TextStyle(
                textSize = 22f,
                color = Color.WHITE,
                positionX = 0.08f, // 8% from left
                positionY = 0.92f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 30f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 45f,
                positionX = 0.88f, // 88% from left (accounting for margins)
                positionY = 0.8f, // 80% from top
                borderWidth = 4f,
                borderColor = Color.argb(255, 186, 85, 211),
                gradientStartColor = Color.argb(180, 255, 255, 255),
                gradientEndColor = Color.argb(100, 186, 85, 211)
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 75, 0, 130),
            lineColor = Color.argb(255, 186, 85, 211),
            textColor = Color.WHITE
        )
    )

    // Design 9: Teal Cyan Theme
    val OverlayPropertyTeal = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.GRADIENT,
            startColor = Color.TRANSPARENT,
            endColor = Color.argb(205, 0, 128, 128), // Teal gradient
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 38f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.BOLD,
                positionX = 0.05f, // 5% from left
                positionY = 0.78f, // 78% from top
                shadowColor = Color.argb(150, 0, 0, 0),
                shadowRadius = 6f,
                shadowOffsetX = 2f,
                shadowOffsetY = 2f
            ),
            businessNameStyle = TextStyle(
                textSize = 26f,
                color = Color.argb(255, 64, 224, 208), // Turquoise
                positionX = 0.05f, // 5% from left
                positionY = 0.85f, // 85% from top
                typeface = TextStyle.FontWeight.BOLD
            ),
            addressStyle = TextStyle(
                textSize = 22f,
                color = Color.WHITE,
                positionX = 0.05f, // 5% from left
                positionY = 0.92f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 30f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 45f,
                positionX = 0.9f, // 90% from left
                positionY = 0.8f, // 80% from top
                borderWidth = 4f,
                borderColor = Color.argb(255, 64, 224, 208),
                gradientStartColor = Color.argb(180, 255, 255, 255),
                gradientEndColor = Color.argb(100, 64, 224, 208)
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 0, 128, 128),
            lineColor = Color.argb(255, 64, 224, 208),
            textColor = Color.WHITE
        )
    )

    // Design 10: Elegant Black Theme
    val OverlayPropertyElegant = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.SOLID,
            solidColor = Color.argb(180, 0, 0, 0), // Black solid
            cornerRadius = 12f,
            marginLeft = 12f,
            marginRight = 12f,
            marginBottom = 12f,
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 38f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.BOLD,
                positionX = 0.07f, // 7% from left (accounting for margins)
                positionY = 0.78f, // 78% from top
                shadowColor = Color.argb(150, 0, 0, 0),
                shadowRadius = 6f,
                shadowOffsetX = 2f,
                shadowOffsetY = 2f
            ),
            businessNameStyle = TextStyle(
                textSize = 26f,
                color = Color.argb(255, 255, 215, 0), // Gold
                positionX = 0.07f, // 7% from left
                positionY = 0.85f, // 85% from top
                typeface = TextStyle.FontWeight.BOLD
            ),
            addressStyle = TextStyle(
                textSize = 22f,
                color = Color.WHITE,
                positionX = 0.07f, // 7% from left
                positionY = 0.92f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 30f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 45f,
                positionX = 0.88f, // 88% from left (accounting for margins)
                positionY = 0.8f, // 80% from top
                borderWidth = 4f,
                borderColor = Color.argb(255, 255, 215, 0),
                gradientStartColor = Color.argb(180, 255, 255, 255),
                gradientEndColor = Color.argb(100, 255, 215, 0)
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 0, 0, 0),
            lineColor = Color.argb(255, 255, 215, 0),
            textColor = Color.WHITE
        )
    )

    // Design 11: Magenta Pink Theme
    val OverlayPropertyMagenta = OverlayProperties(
        bottomBackground = BottomBackgroundStyle(
            type = BackgroundType.GRADIENT,
            startColor = Color.TRANSPARENT,
            endColor = Color.argb(195, 199, 21, 133), // Medium violet red gradient
            gradientHeight = 200f
        ),
        userProfile = UserProfileStyle(
            nameStyle = TextStyle(
                textSize = 38f,
                color = Color.WHITE,
                typeface = TextStyle.FontWeight.BOLD,
                positionX = 0.05f, // 5% from left
                positionY = 0.78f, // 78% from top
                shadowColor = Color.argb(150, 0, 0, 0),
                shadowRadius = 6f,
                shadowOffsetX = 2f,
                shadowOffsetY = 2f
            ),
            businessNameStyle = TextStyle(
                textSize = 26f,
                color = Color.argb(255, 255, 105, 180), // Hot pink
                positionX = 0.05f, // 5% from left
                positionY = 0.85f, // 85% from top
                typeface = TextStyle.FontWeight.BOLD
            ),
            addressStyle = TextStyle(
                textSize = 22f,
                color = Color.WHITE,
                positionX = 0.05f, // 5% from left
                positionY = 0.92f, // 92% from top
                maxWordsPerLine = 4,
                lineSpacing = 30f
            ),
            profileCircle = ProfileCircleStyle(
                radius = 45f,
                positionX = 0.9f, // 90% from left
                positionY = 0.8f, // 80% from top
                borderWidth = 4f,
                borderColor = Color.argb(255, 255, 105, 180),
                gradientStartColor = Color.argb(180, 255, 255, 255),
                gradientEndColor = Color.argb(100, 255, 105, 180)
            )
        ),
        topWatermark = TopWatermarkStyle(
            backgroundColor = Color.argb(255, 199, 21, 133),
            lineColor = Color.argb(255, 255, 105, 180),
            textColor = Color.WHITE
        )
    )

    // Complete collection of all overlay styles
    val currentStyles = listOf(
        OverlayPropertyOne,
        OverlayPropertyTwo,
        OverlayPropertyThree,
        OverlayPropertyOcean,
        OverlayPropertySunset,
        OverlayPropertyForest,
        OverlayPropertyCrimson,
        OverlayPropertyRoyal,
        OverlayPropertyTeal,
        OverlayPropertyElegant,
        OverlayPropertyMagenta
    )


}