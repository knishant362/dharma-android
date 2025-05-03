package com.aurora.app.ui.screens.tarotSelect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.domain.repo.TarotRepository
import com.aurora.app.utils.Constants.PACK_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TarotSelectViewModel @Inject constructor(
    private val repository: TarotRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TarotSelectUIState())
    val uiState: StateFlow<TarotSelectUIState> = _uiState

    fun setupSpread(spreadDetail: SpreadDetail) {
        _uiState.value = _uiState.value.copy(
            spreadDetail = spreadDetail,
            maxSelectedCards = spreadDetail.cards.size
        )
        loadCardsData()
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
                loadPreviousResult()
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(isLoading = false, error = e.message ?: "Unknown Error")
            }
        }
    }

    private fun loadPreviousResult() = viewModelScope.launch {
        with(uiState.value){
            val spreadResults = spreadDetail?.let { mainRepository.getSpreadResultBySpreadId(it.id) }
            val lastResult = spreadResults?.firstOrNull()
            if (lastResult == null){
                Timber.e("loadPreviousResult: spreadId: ${spreadDetail?.id} No previous result found")
            } else {
                Timber.e("loadPreviousResult: spreadId: ${spreadDetail?.id} lastResult:$lastResult")
                val selectedCards = selectableCards.filter { card -> lastResult.selectedCardIds.contains(card.cardId) }
                _uiState.update {
                    it.copy(
                        selectedCards = selectedCards,
                        isRevealed = true
                    )
                }
            }
        }
    }

    fun addSelectedCard(card: SelectableTarotCard) {
        _uiState.update { currentState ->
            currentState.copy(selectedCards = currentState.selectedCards + card)
        }
    }

    fun clearSelectedCards() {
        _uiState.update { currentState ->
            currentState.copy(selectedCards = emptyList(), isRevealed = false)
        }
    }

    fun setRevealed(revealed: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isRevealed = revealed)
        }
        uiState.value.spreadDetail?.let { saveCompletedSpread(it) }
    }

    fun saveCompletedSpread(spreadDetail: SpreadDetail) {
        viewModelScope.launch {
            val selectedIds = uiState.value.selectedCards.map { it.cardId }
            mainRepository.saveSpread(spreadDetail.id, selectedIds)
            Timber.e("saveCompletedSpread: $selectedIds, ${spreadDetail.id}")
        }
    }
}
