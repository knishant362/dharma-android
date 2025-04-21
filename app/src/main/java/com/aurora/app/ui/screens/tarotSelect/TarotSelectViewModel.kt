package com.aurora.app.ui.screens.tarotSelect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.domain.repo.TarotRepository
import com.aurora.app.utils.Constants.PACK_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TarotSelectViewModel @Inject constructor(
    private val repository: TarotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TarotSelectUIState())
    val uiState: StateFlow<TarotSelectUIState> = _uiState

    init {
        loadCardsData()
    }

    fun setupSpread(spreadDetail: SpreadDetail) {
        _uiState.value = _uiState.value.copy(
            spreadDetail = spreadDetail,
            maxSelectedCards = spreadDetail.cards.size
        )
    }

    private fun loadCardsData() {
        viewModelScope.launch {
            try {
                val cards = repository.loadTarotCards(packName = PACK_NAME)
                Timber.e("cards: $cards")
                val selectableCards =
                    cards.shuffled().mapIndexed { index, it -> it.toSelectableTarotCard(index) }
                Timber.e("selectableCards: $selectableCards")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    cards = cards,
                    selectableCards = selectableCards
                )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(isLoading = false, error = e.message ?: "Unknown Error")
            }
        }
    }
}
