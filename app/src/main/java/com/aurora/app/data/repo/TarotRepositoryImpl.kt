package com.aurora.app.data.repo

import android.content.Context
import com.aurora.app.data.model.TarotCardDto
import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.model.spread.CardInfo
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.domain.repo.TarotRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

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


    override fun loadSpreadDetails(): List<SpreadDetail> {
        return try {
            val json = context.assets.open("data/main.json").bufferedReader().use { it.readText() }
            val spreadsJson = JSONObject(json).getJSONObject("spreads").getJSONObject("spreads")

            val spreads = mutableListOf<SpreadDetail>()
            spreadsJson.keys().forEach { key ->
                val obj = spreadsJson.getJSONObject(key)
                val title = obj.optString("title")
                val description = obj.optString("description")
                val cards = obj.optJSONArray("cards")?.let { array ->
                    List(array.length()) { i ->
                        val card = array.getJSONObject(i)
                        CardInfo(
                            name = card.optString("name"),
                            description = card.optString("description")
                        )
                    }
                } ?: emptyList()

                spreads.add(SpreadDetail(id = key, title = title, description = description, cards = cards))
            }
            spreads
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
