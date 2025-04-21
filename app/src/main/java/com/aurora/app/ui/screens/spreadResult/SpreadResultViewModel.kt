package com.aurora.app.ui.screens.spreadResult

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
class SpreadResultViewModel @Inject constructor(
    private val repository: TarotRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<SpreadResultUIState>(SpreadResultUIState.Loading)
    val uiState: State<SpreadResultUIState> = _uiState

    fun setupResult(){

    }

    private fun loadSpreadDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                val spreads = repository.loadSpreadDetails().filter { it.title in listOf("Daily Reading", "Single Card", "Past, Present, Future") }.distinct()
//                _uiState.value = SpreadResultUIState.Success()
            } catch (e: Exception) {
                _uiState.value =
                    SpreadResultUIState.Error(e.message ?: "Error loading spreads")
            }
        }
    }


}