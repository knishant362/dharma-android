package com.aurora.app.ui.screens.tarotSelect

import com.aurora.app.R
import com.aurora.app.domain.model.TarotCard

data class SelectableTarotCard(
    val id: Int,
    val cardId: String,
    val backImageRes: Int,
    val frontImage: String,
    var isFlipped: Boolean = false
)

fun TarotCard.toSelectableTarotCard(index: Int): SelectableTarotCard {
    return SelectableTarotCard(
        id = index,
        cardId = id,
        backImageRes = R.drawable.card_back,
        frontImage = imagePath
    )
}