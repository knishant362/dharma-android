package com.aurora.app.domain.model.spread

import android.os.Parcelable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.aurora.app.R
import com.aurora.app.data.model.SpreadResult
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpreadDetailDTO(
    val index: Int,
    val id: String,
    val title: String,
    val description: String,
    val icon: Int = R.drawable.ic_four_card,
    val cardIcon: Int = R.drawable.ic_one_card,
    val cards: List<CardInfo> = emptyList(),
    val spreadResult: SpreadResult?
): Parcelable

fun SpreadDetail.toSpreadDetailDTO(index: Int, spreadResult: SpreadResult?): SpreadDetailDTO {
    return SpreadDetailDTO(
        index= index,
        id = id,
        title = title,
        description = description,
        icon = icon,
        cardIcon = cardIcon,
        cards = cards,
        spreadResult = spreadResult
    )
}