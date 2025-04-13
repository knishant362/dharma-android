package com.aurora.app.ui.screens.spreadDetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.R
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.domain.repo.TarotRepository
import com.aurora.app.ui.screens.tarotSelect.SelectableTarotCard
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
                val spreads = repository.loadSpreadDetails().filter { it.title in listOf("Daily Reading", "Single Card", "Past, Present, Future") }.distinct()
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
            description = "These three cards will provide a general of your day with some health",
            cards = emptyList(),
            icon = R.drawable.ic_three_card
        ),
        SpreadDetail(
            id = "1",
            title = "The Daily Path",
            description = "Through this path you will find the answers for today about work, money, love and more.",
            cards = emptyList(),
            icon = R.drawable.ic_four_card
        ),
        SpreadDetail(
            id = "2",
            title = "Couple's Tarot",
            description = "Discover the Tarot forecast for you and your partner.",
            cards = emptyList(),
            icon = R.drawable.ic_two_card
        ),
        SpreadDetail(
            id = "3",
            title = "Card of the Day",
            description = "Pick a card that will help you discover what you are destined to experience today.",
            cards = emptyList(),
            icon = R.drawable.ic_one_card
        ),
        SpreadDetail(
            id = "4",
            title = "Yes or no",
            description = "Take a deep breath and think a yes or no question. Pick a card it will tell you the answer",
            cards = emptyList(),
            icon = R.drawable.ic_one_card
        ),
    )

}