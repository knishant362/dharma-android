package com.aurora.app.domain.model

data class SelectableCard(
    val id: Int,
    val imageRes: String, // tarot back image or real card
    val isSelected: Boolean = false
)
