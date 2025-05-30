package com.aurora.app.ui.screens.spreadList

import com.aurora.app.data.model.SpreadResult
import com.aurora.app.domain.model.spread.SpreadDetail

sealed class SpreadDetailUiState {
    object Loading : SpreadDetailUiState()
    data class Success(
        val spreadResults: List<SpreadResult> = emptyList(),
        val todayResults: List<SpreadResult> = emptyList(),
        val spreads: List<SpreadDetail> = emptyList()
    ) : SpreadDetailUiState()

    data class Error(val message: String) : SpreadDetailUiState()
}
