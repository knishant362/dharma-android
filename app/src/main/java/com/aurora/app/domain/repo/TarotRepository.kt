package com.aurora.app.domain.repo

import com.aurora.app.domain.model.TarotCard

interface TarotRepository {

    fun loadTarotCards(): List<TarotCard>

}