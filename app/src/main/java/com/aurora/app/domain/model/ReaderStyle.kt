package com.aurora.app.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensityLarge
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.ui.graphics.vector.ImageVector
import com.aurora.app.data.model.ReaderStyleModel

data class ReaderStyle(
    val fontSize: Float,
    val lineHeight: Float,
    val font: String,
    val darkTheme: Boolean
) {
    companion object {

        const val MinimumFontSize = 24f
        val fontSizes = listOf(
            MinimumFontSize - 2f,
            MinimumFontSize,
            MinimumFontSize + 2f,
            MinimumFontSize + 4f,
            MinimumFontSize + 6f,
            MinimumFontSize + 8f,
        )

        const val MinimumLineHeight = 1.5f
        val lineHeights = listOf(
            LineHeightStyle(Icons.Default.DensitySmall, 1.0f),
            LineHeightStyle(Icons.Default.DensityMedium, MinimumLineHeight),
            LineHeightStyle(Icons.Default.DensityLarge, 2.0f)
        )

        val DefaultFontFamily = "yatra"

        val fonts = listOf(
            "poppins",
            "biryani",
            "gotu",
            "kalam",
            "noto",
            "yatra",
        )

        val Default = ReaderStyle(
            fontSize = MinimumFontSize,
            lineHeight = MinimumLineHeight,
            font = DefaultFontFamily,
            darkTheme = false
        )
    }
}


data class LineHeightStyle(
    val icon: ImageVector,
    val value: Float
)

fun ReaderStyleModel.toReaderStyle(): ReaderStyle {
    val default = ReaderStyle.Default
    return ReaderStyle(
        fontSize = fontSize ?: default.fontSize,
        font = fontFamily ?: default.font,
        lineHeight = lineHeight ?: default.lineHeight,
        darkTheme = darkTheme ?: default.darkTheme
    )
}
