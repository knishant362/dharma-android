package com.aurora.app.ui.screens.spreadDetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.domain.repo.TarotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpreadViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val repository: TarotRepository
) : ViewModel() {

    private val _spreadUiState = mutableStateOf<SpreadDetailUiState>(SpreadDetailUiState.Loading)
    val spreadUiState: State<SpreadDetailUiState> = _spreadUiState

    fun loadSpreadDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                val spreads = repository.loadSpreadDetails().filter { it.title in listOf("Daily Reading", "Single Card", "Past, Present, Future") }.distinct()
                val results = mainRepository.getSavedSpreads()
                _spreadUiState.value = SpreadDetailUiState.Success(spreadResults = results, spreads = repository.getAllSpreads())
            } catch (e: Exception) {
                _spreadUiState.value =
                    SpreadDetailUiState.Error(e.message ?: "Error loading spreads")
            }
        }
    }

}