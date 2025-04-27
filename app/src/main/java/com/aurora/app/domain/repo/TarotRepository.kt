package com.aurora.app.domain.repo

import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.model.spread.FullSpreadDetail
import com.aurora.app.domain.model.spread.SpreadDetail

interface TarotRepository {

    fun getAllCardTypes(packName: String): Set<String>

    fun getAllSpreads(): List<SpreadDetail>

    fun loadFullSpreadDetail(packName: String, cardName: String): FullSpreadDetail?

    fun loadSpreadDetails(): List<SpreadDetail>

    fun loadTarotCards(packName: String): List<TarotCard>

}