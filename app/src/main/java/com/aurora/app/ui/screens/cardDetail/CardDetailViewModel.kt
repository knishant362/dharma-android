package com.aurora.app.ui.screens.cardDetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aurora.app.R
import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.model.spread.Property
import com.aurora.app.domain.repo.TarotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val repository: TarotRepository
) : ViewModel() {
    fun initialSetup(tarotCard: TarotCard) {

        _uiState.value = CardDetailUIState(
            title = tarotCard.name,
            imagePath = tarotCard.imagePath,
            tags = tarotCard.keywords ?: emptyList(),
            affirmation = tarotCard.affirmation,
            description = tarotCard.description,
            properties = listOf(  //Todo : use real value here
                Property("Suit", "Major"),
                Property("Astrology", "Moon"),
                Property("Element", "Water"),
                Property("Yes or No", "Maybe")
            )
        )

    }

    private val _uiState = mutableStateOf(CardDetailUIState())
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