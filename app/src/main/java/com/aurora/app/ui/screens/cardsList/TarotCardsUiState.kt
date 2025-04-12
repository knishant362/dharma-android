package com.aurora.app.ui.screens.cardsList

import com.aurora.app.domain.model.TarotCard

sealed class TarotUiState {
    data object Loading : TarotUiState()
    data class Success(val cards: List<TarotCard>) : TarotUiState()
    data class Error(val message: String) : TarotUiState()
}
