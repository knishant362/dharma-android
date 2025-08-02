package com.aurora.app.data.model

import com.aurora.app.domain.model.ReaderStyle

data class ReaderStyleModel(
    val fontSize: Float?,
    val fontFamily: String?,
    val lineHeight: Float?,
    val darkTheme: Boolean?
)

fun ReaderStyle.toReaderStyleModel(): ReaderStyleModel {
    return ReaderStyleModel(
        fontSize = fontSize,
        fontFamily = font,
        lineHeight = lineHeight,
        darkTheme = darkTheme
    )
}