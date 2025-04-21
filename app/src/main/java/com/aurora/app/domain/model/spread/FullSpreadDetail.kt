package com.aurora.app.domain.model.spread

import com.aurora.app.R

data class FullSpreadDetail(
    val title: String = "",
    val imageRes: Int = R.drawable.ic_one_card,
    val tags: List<String> = emptyList(),
    val affirmation: String = "",
    val description: String = "",
    val properties: List<Property> = emptyList()
)