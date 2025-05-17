package com.aurora.app.ui.screens.dasbboard

import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.domain.model.dashboard.TarotOption

data class DashboardUiState(
    val isLoading: Boolean = true,
    val errorMessages: String = "",
    val featuredItems: List<Featured> = emptyList(),
    val tarotOptions: List<TarotOption> = emptyList()
)