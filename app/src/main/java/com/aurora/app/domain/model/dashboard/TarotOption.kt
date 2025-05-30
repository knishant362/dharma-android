package com.aurora.app.domain.model.dashboard

import com.aurora.app.domain.model.spread.SpreadDetail

data class TarotOption(
    val id: Long,
    val title: String,
    val iconRes: Int,
    val spreadDetail: SpreadDetail? = null
)
