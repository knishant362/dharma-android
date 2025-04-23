package com.aurora.app.data.repo

import android.content.Context
import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.model.spread.CardInfo
import com.aurora.app.domain.model.spread.FullSpreadDetail
import com.aurora.app.domain.model.spread.Property
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.domain.repo.TarotRepository
import com.aurora.app.utils.Constants.CONTENT_FILE
import com.aurora.app.utils.Constants.IMAGE_DIRECTORY
import org.json.JSONObject
import timber.log.Timber

class TarotRepositoryImpl(private val context: Context): TarotRepository {

    override fun getAllCardTypes(packName: String): Set<String> {
        return try {
            val jsonStr = context.assets.open(CONTENT_FILE).bufferedReader().use { it.readText() }
            val cardsObject = JSONObject(jsonStr).getJSONObject("AIS")

            buildSet {
                cardsObject.keys().forEach { key ->
                    val type = key.substringBefore("-")
                    add(type)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptySet()
        }
    }

    override fun loadTarotCards(packName: String): List<TarotCard> {
        try {
            val json = context.assets.open(CONTENT_FILE).bufferedReader().use { it.readText() }
            val imagesJson = context.assets.open("data/$packName.json").bufferedReader().use { it.readText() }
            val imageData = JSONObject(imagesJson)
            val extraDetails = JSONObject(json).getJSONObject("meditations")
            val cardsObject = JSONObject(json).getJSONObject("tarot").getJSONObject(packName)
            val cardList = mutableListOf<TarotCard>()

            cardsObject.keys().forEach { key ->
                val cardJson = cardsObject.getJSONObject(key)
                val type = key.substringBeforeLast("-") // "cups-38.png" -> "cups"
                val id = key.substringBeforeLast(".")
                val extension = key.substringAfterLast(".")
                val affirmation = extraDetails.getJSONObject(id).getJSONObject(extension).optString("0")

                val card = TarotCard(
                    id = id,
                    name = cardJson.optString("title"),
                    description = cardJson.optString("description"),
                    keywords = cardJson.optJSONArray("keywords")?.let { array ->
                        List(array.length()) { i -> array.getString(i) }
                    } ?: emptyList(),
                    type = type,
                    affirmation = affirmation,
                    image = "images/$packName/${imageData.optString(id)}"
                )
                cardList.add(card)
            }
            return cardList
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }


    override fun loadSpreadDetails(): List<SpreadDetail> {
        return try {
            val json = context.assets.open(CONTENT_FILE).bufferedReader().use { it.readText() }
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

    override fun loadFullSpreadDetail(packName: String, cardName: String): FullSpreadDetail? {
        return try {
            val extension = "png"
            val json = context.assets.open(CONTENT_FILE).bufferedReader().use { it.readText() }
            val spreadsJson = JSONObject(json).getJSONObject("tarot").getJSONObject(packName)

            val obj = spreadsJson.getJSONObject("$cardName.$extension")
            val title = obj.optString("title")
            val description = obj.optString("description")
            val keywords = obj.optJSONArray("keywords")?.let { array ->
                List(array.length()) { i -> array.getString(i) }
            } ?: emptyList()

            val meditations = JSONObject(json).getJSONObject("meditations").getJSONObject(cardName).getJSONObject(extension)
            val affirmation = meditations.optString("0")

            val spreadDetail = FullSpreadDetail(
                title = title,
                tags = keywords,
                affirmation = affirmation, // Static or derived from another source
                description = description,
                properties = listOf(
                    Property("Suit", "Major"),
                    Property("Astrology", "Moon"),
                    Property("Element", "Water"),
                    Property("Yes or No", "Maybe")
                )
            )
            spreadDetail
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }
}
