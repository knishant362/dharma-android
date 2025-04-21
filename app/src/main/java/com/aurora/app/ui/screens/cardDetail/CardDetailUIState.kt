package com.aurora.app.ui.screens.cardDetail

import com.aurora.app.R

data class CardDetailUIState(
    val title: String = "",
    val imageRes: Int = R.drawable.ic_one_card, // your default placeholder
    val tags: List<String> = emptyList(),
    val affirmation: String = "",
    val description: String = "",
    val properties: List<Property> = emptyList()
)

data class Property(val title: String, val value: String)
