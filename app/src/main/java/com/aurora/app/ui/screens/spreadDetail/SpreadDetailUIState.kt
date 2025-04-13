package com.aurora.app.ui.screens.spreadDetail

import com.aurora.app.domain.model.spread.SpreadDetail

sealed class SpreadDetailUiState {
    object Loading : SpreadDetailUiState()
    data class Success(val spreads: List<SpreadDetail>) : SpreadDetailUiState()
    data class Error(val message: String) : SpreadDetailUiState()
}
