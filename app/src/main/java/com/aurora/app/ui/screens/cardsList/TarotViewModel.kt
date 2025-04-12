package com.aurora.app.ui.screens.cardsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.repo.TarotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TarotViewModel @Inject constructor(
    private val repository: TarotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TarotUiState>(TarotUiState.Loading)
    val uiState: StateFlow<TarotUiState> = _uiState

    private val _filters = MutableStateFlow<List<String>>(emptyList())
    val filters: StateFlow<List<String>> = _filters

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cards = repository.loadTarotCards()
                _uiState.value = TarotUiState.Success(cards)

                val dynamicFilters = buildList {
                    add("All")
                    addAll(cards.map { it.type }.filterNot { it == "Minor" }.distinct())
                    addAll(cards.mapNotNull { it.suit }.distinct())
                }
                _filters.value = dynamicFilters.distinct()

            } catch (e: Exception) {
                _uiState.value = TarotUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
