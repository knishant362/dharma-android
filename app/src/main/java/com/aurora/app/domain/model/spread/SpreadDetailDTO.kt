package com.aurora.app.domain.model.spread

import android.os.Parcelable
import com.aurora.app.R
import com.aurora.app.data.model.SpreadResult
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpreadDetailDTO(
    val id: String,
    val title: String,
    val description: String,
    val icon: Int = R.drawable.ic_four_card,
    val cards: List<CardInfo> = emptyList(),
    val spreadResult: SpreadResult?
): Parcelable

fun SpreadDetail.toSpreadDetailDTO(spreadResult: SpreadResult?): SpreadDetailDTO {
    return SpreadDetailDTO(
        id = id,
        title = title,
        description = description,
        icon = icon,
        cards = cards,
        spreadResult = spreadResult
    )
}