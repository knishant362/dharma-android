package com.aurora.app.data.repo

import android.content.Context
import com.aurora.app.R
import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.model.spread.CardInfo
import com.aurora.app.domain.model.spread.FullSpreadDetail
import com.aurora.app.domain.model.spread.Property
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.domain.repo.TarotRepository
import com.aurora.app.utils.Constants.CONTENT_FILE
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

    override fun getAllSpreads(): List<SpreadDetail> {

        val spreadList = listOf(
            SpreadDetail(
                id = "0",
                title = "Classic Spread",
                description = "These three cards will provide a general overview of your day including health.",
                cards = listOf(
                    CardInfo("Past", "Reveals the influence of past events on your current situation."),
                    CardInfo("Present", "Describes your current emotional, physical, or mental state."),
                    CardInfo("Future", "Suggests what you may encounter or need to be prepared for.")
                ),
                icon = R.drawable.ic_three_card
            ),
            SpreadDetail(
                id = "1",
                title = "The Daily Path",
                description = "Through this path you will find answers for today about work, money, love and more.",
                cards = listOf(
                    CardInfo("Work", "Insight into your career or professional tasks today."),
                    CardInfo("Money", "What to expect financially or materially."),
                    CardInfo("Love", "An overview of your emotional or romantic life today."),
                    CardInfo("Spiritual", "A message to guide your inner path today.")
                ),
                icon = R.drawable.ic_four_card
            ),
            SpreadDetail(
                id = "2",
                title = "Couple's Tarot",
                description = "Discover the Tarot forecast for you and your partner.",
                cards = listOf(
                    CardInfo("You", "Represents your feelings and role in the relationship."),
                    CardInfo("Partner", "Reveals your partner’s perspective or emotions.")
                ),
                icon = R.drawable.ic_two_card
            ),
            SpreadDetail(
                id = "3",
                title = "Card of the Day",
                description = "Pick a card to discover what you're destined to experience today.",
                cards = listOf(
                    CardInfo("Daily Guidance", "A card to guide your choices and mindset today.")
                ),
                icon = R.drawable.ic_one_card
            ),
            SpreadDetail(
                id = "4",
                title = "Yes or No",
                description = "Take a deep breath and think of a yes or no question. Pick a card—it will reveal the answer.",
                cards = listOf(
                    CardInfo("Answer", "This card will indicate a yes, no, or maybe based on your energy.")
                ),
                icon = R.drawable.ic_one_card
            ),
            SpreadDetail(
                id = "5",
                title = "My Love Life Developments",
                description = "These three cards will provide a general overview of your day including health.",
                cards = listOf(
                    CardInfo("Past", "Reveals the influence of past events on your current situation."),
                    CardInfo("Present", "Describes your current emotional, physical, or mental state."),
                    CardInfo("Future", "Suggests what you may encounter or need to be prepared for.")
                ),
                icon = R.drawable.ic_three_card
            ),
            SpreadDetail(
                id = "6",
                title = "My Prosperity and Wellbeing",
                description = "These three cards will provide a general overview of your day including health.",
                cards = listOf(
                    CardInfo("Past", "Reveals the influence of past events on your current situation."),
                    CardInfo("Present", "Describes your current emotional, physical, or mental state."),
                    CardInfo("Future", "Suggests what you may encounter or need to be prepared for.")
                ),
                icon = R.drawable.ic_three_card
            ),
            SpreadDetail(
                id = "7",
                title = "What Awaits Me Soon",
                description = "These three cards will provide a general overview of your day including health.",
                cards = listOf(
                    CardInfo("Past", "Reveals the influence of past events on your current situation."),
                    CardInfo("Present", "Describes your current emotional, physical, or mental state."),
                    CardInfo("Future", "Suggests what you may encounter or need to be prepared for.")
                ),
                icon = R.drawable.ic_three_card
            ),
        )

        return spreadList
    }

    override fun getExploreSpreads(): List<SpreadDetail> {
        val spreadList = listOf(
            SpreadDetail(
                id = "5",
                title = "My Love Life Developments",
                description = "These three cards will provide a general overview of your day including health.",
                cards = listOf(
                    CardInfo("Past", "Reveals the influence of past events on your current situation."),
                    CardInfo("Present", "Describes your current emotional, physical, or mental state."),
                    CardInfo("Future", "Suggests what you may encounter or need to be prepared for.")
                ),
                icon = R.drawable.ic_three_card
            ),
            SpreadDetail(
                id = "6",
                title = "My Prosperity and Wellbeing",
                description = "These three cards will provide a general overview of your day including health.",
                cards = listOf(
                    CardInfo("Past", "Reveals the influence of past events on your current situation."),
                    CardInfo("Present", "Describes your current emotional, physical, or mental state."),
                    CardInfo("Future", "Suggests what you may encounter or need to be prepared for.")
                ),
                icon = R.drawable.ic_three_card
            ),
            SpreadDetail(
                id = "7",
                title = "What Awaits Me Soon",
                description = "These three cards will provide a general overview of your day including health.",
                cards = listOf(
                    CardInfo("Past", "Reveals the influence of past events on your current situation."),
                    CardInfo("Present", "Describes your current emotional, physical, or mental state."),
                    CardInfo("Future", "Suggests what you may encounter or need to be prepared for.")
                ),
                icon = R.drawable.ic_three_card
            )
        )

        return spreadList
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
                    imagePath = "images/$packName/${imageData.optString(id)}"
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
