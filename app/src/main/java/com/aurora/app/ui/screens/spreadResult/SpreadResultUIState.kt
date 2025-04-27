package com.aurora.app.ui.screens.spreadResult

import com.aurora.app.data.model.SpreadResult
import com.aurora.app.domain.model.TarotCard

data class SpreadResultUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val time: String = "",
    val result: SpreadResult? = null,
    val tarotCards: List<TarotCard> = emptyList()
)