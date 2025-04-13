package com.aurora.app.ui.screens.spreadResult

import com.aurora.app.domain.model.spread.SpreadDetail

sealed class SpreadResultUIState {
    data object Loading : SpreadResultUIState()
    data class Success(val spreads: List<SpreadDetail>) : SpreadResultUIState()
    data class Error(val message: String) : SpreadResultUIState()
}