package com.aurora.app.ui.screens.spreadDetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.R
import com.aurora.app.domain.model.spread.CardInfo
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.domain.repo.TarotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpreadViewModel @Inject constructor(
    private val repository: TarotRepository
) : ViewModel() {

    private val _spreadUiState = mutableStateOf<SpreadDetailUiState>(SpreadDetailUiState.Loading)
    val spreadUiState: State<SpreadDetailUiState> = _spreadUiState

    init {
        loadSpreadDetails()
    }

    private fun loadSpreadDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                val spreads = repository.loadSpreadDetails().filter { it.title in listOf("Daily Reading", "Single Card", "Past, Present, Future") }.distinct()
                _spreadUiState.value = SpreadDetailUiState.Success(spreadList)
            } catch (e: Exception) {
                _spreadUiState.value =
                    SpreadDetailUiState.Error(e.message ?: "Error loading spreads")
            }
        }
    }

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
    )

}