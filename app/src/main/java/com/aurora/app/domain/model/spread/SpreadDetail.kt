package com.aurora.app.domain.model.spread

data class SpreadDetail(
    val id: String,
    val title: String,
    val description: String,
    val icon: Int = 0,
    val cards: List<CardInfo> = emptyList()
)