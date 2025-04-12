package com.aurora.app.domain.model

data class TarotCard(
    val id: String,
    val name: String,
    val type: String,
    val suit: String?,
    val number: Int?,
    val imageRes: String,
    val meaningUpright: String?,
    val meaningReversed: String?,
    val keywords: List<String>?
)
