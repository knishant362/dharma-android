package com.aurora.app.data.model

data class SpreadResult(
    val spreadDetailId: String,
    val selectedCardIds: List<String>,
    val createdAt: Long = System.currentTimeMillis()
)