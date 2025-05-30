package com.aurora.app.domain.model.spread

import android.os.Parcelable
import com.aurora.app.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpreadDetail(
    val id: String,
    val title: String,
    val description: String,
    val icon: Int = R.drawable.ic_four_card,
    val cardIcon: Int = R.drawable.ic_one_card,
    val cards: List<CardInfo> = emptyList()
): Parcelable

fun SpreadDetailDTO.toSpreadDetail(): SpreadDetail {
    return SpreadDetail(
        id = id,
        title = title,
        description = description,
        icon = icon,
        cards = cards
    )
}