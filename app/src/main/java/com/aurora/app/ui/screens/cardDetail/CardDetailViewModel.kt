package com.aurora.app.ui.screens.cardDetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.R
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.domain.repo.TarotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val repository: TarotRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(
        CardDetailUIState(
            title = "The High Priestess",
            imageRes = R.drawable.ic_one_card,
            tags = listOf("INTUITION", "UNCONSCIOUS", "INNER VOICE"),
            affirmation = "I trust the wisdom of my inner voice.",
            description = "The high priestess is the guardian of the inner world, where night reigns and the unconscious holds sway of our actions. She beckons you into the world within, for she wishes you to understand yourself - your true self, and not just the face that we present to the world outside us. She initiates you on an inner journey, and knows that your answers are hidden within. Follow your intuition, she will not lead you astray.",
            properties = listOf(
                Property("Suit", "Major"),
                Property("Astrology", "Moon"),
                Property("Element", "Water"),
                Property("Yes or No", "Maybe")
            )
        )
    )
    val uiState: State<CardDetailUIState> = _uiState

//    private fun loadSpreadDetails() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val spreads = repository.loadSpreadDetails().filter { it.title in listOf("Daily Reading", "Single Card", "Past, Present, Future") }.distinct()
//                _uiState.value = CardDetailUIState.Success(spreads)
//            } catch (e: Exception) {
//                _uiState.value =
//                    CardDetailUIState.Error(e.message ?: "Error loading spreads")
//            }
//        }
//    }


}