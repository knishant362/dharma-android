package com.aurora.app.domain.repo

import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.model.spread.SpreadDetail

interface TarotRepository {

    fun loadSpreadDetails(): List<SpreadDetail>

    fun loadTarotCards(): List<TarotCard>

}