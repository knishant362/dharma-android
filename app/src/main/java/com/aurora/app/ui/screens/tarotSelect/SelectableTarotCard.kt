package com.aurora.app.ui.screens.tarotSelect

import com.aurora.app.R
import com.aurora.app.domain.model.TarotCard

data class SelectableTarotCard(
    val id: Int,
    val cardId: String,
    val frontImageRes: Int,
    val backImageRes: Int,
    var isFlipped: Boolean = false
)

fun TarotCard.toSelectableTarotCard(index: Int): SelectableTarotCard {
    return SelectableTarotCard(
        id = index,
        cardId = id,
        frontImageRes = R.drawable.card_front,
        backImageRes = R.drawable.card_back
    )
}