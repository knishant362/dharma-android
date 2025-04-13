package com.aurora.app.ui.screens.cardDetail

import com.aurora.app.domain.model.spread.SpreadDetail

sealed class CardDetailUIState {
    data object Loading : CardDetailUIState()
    data class Success(val spreads: List<SpreadDetail>) : CardDetailUIState()
    data class Error(val message: String) : CardDetailUIState()
}