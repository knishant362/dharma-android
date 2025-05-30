package com.aurora.app.ui.screens.dasbboard

import com.aurora.app.data.model.SpreadResult
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.domain.model.dashboard.TarotOption
import com.aurora.app.domain.model.spread.SpreadDetailDTO

data class DashboardUiState(
    val isLoading: Boolean = true,
    val errorMessages: String = "",
    val featuredItems: List<Featured> = emptyList(),
    val tarotOptions: List<TarotOption> = emptyList(),
    val spreads: List<SpreadDetailDTO> = emptyList(),
    val spreadResults: List<SpreadResult> = emptyList(),
    val todayResults: List<SpreadResult> = emptyList()
)