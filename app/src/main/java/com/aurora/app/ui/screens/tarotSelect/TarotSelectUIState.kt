package com.aurora.app.ui.screens.tarotSelect

import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.model.spread.SpreadDetail

data class TarotSelectUIState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val spreadDetail: SpreadDetail? = null,
    val cards: List<TarotCard> = emptyList(),
    val selectableCards: List<SelectableTarotCard> = emptyList(),
    val maxSelectedCards: Int = 0,
    val selectedCards: List<SelectableTarotCard> = emptyList(),
    val isRevealed: Boolean = false,
    val waitTimeInSeconds: Int = 0,
    val loadingTimeThreshold: Int = 100
)