package com.aurora.app.data.repo

import android.content.Context
import com.aurora.app.data.model.TarotCardDto
import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.repo.TarotRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TarotRepositoryImpl(private val context: Context): TarotRepository {

    override fun loadTarotCards(): List<TarotCard> {
        try {
            val json = context.assets.open("data/tarot_cards.json").bufferedReader().use { it.readText() }
            val dtoList: List<TarotCardDto> = Gson().fromJson(json, object : TypeToken<List<TarotCardDto>>() {}.type)
            return dtoList.map { it.toDomain() }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}
