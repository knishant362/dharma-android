package com.aurora.app.ui.screens.cardDetail

import com.aurora.app.domain.model.spread.Property

data class CardDetailUIState(
    val title: String = "",
    val imagePath: String = "",
    val tags: List<String> = emptyList(),
    val affirmation: String = "",
    val description: String = "",
    val properties: List<Property> = emptyList()
)
