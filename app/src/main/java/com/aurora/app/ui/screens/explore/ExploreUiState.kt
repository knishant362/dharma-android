package com.aurora.app.ui.screens.explore

import com.aurora.app.data.model.SpreadResult
import com.aurora.app.domain.model.spread.SpreadDetailDTO

data class ExploreUiState(
    val isLoading: Boolean = true,
    val errorMessages: String = "",
    val title: String = "",
    val subtitle: String = "",
    val spreads: List<SpreadDetailDTO> = emptyList(),
    val spreadResults: List<SpreadResult> = emptyList(),
    val todayResults: List<SpreadResult> = emptyList(),
)