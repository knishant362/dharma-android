package com.aurora.app.ui.screens.spreadResult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.domain.repo.TarotRepository
import com.aurora.app.utils.Constants
import com.aurora.app.utils.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpreadResultViewModel @Inject constructor(
    private val repository: MainRepository,
    private val tarotRepository: TarotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpreadResultUIState())
    val uiState: StateFlow<SpreadResultUIState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SpreadResultUiEvent>()
    val eventFlow: SharedFlow<SpreadResultUiEvent> = _eventFlow.asSharedFlow()


    fun setupResult(spreadDetailId: String) = viewModelScope.launch {
        val spreadResult = repository.getSpreadResultBySpreadId(spreadDetailId).firstOrNull()
        val tarotCards = tarotRepository.loadTarotCards(Constants.PACK_NAME)
            .filter { spreadResult?.selectedCardIds?.contains(it.id) == true }
        val time = TimeUtil.formatTimestampToMonthDay(
            spreadResult?.createdAt ?: System.currentTimeMillis()
        )

        _uiState.value = _uiState.value.copy(
            time = time,
            result = spreadResult,
            tarotCards = tarotCards
        )
    }

    fun onDrawAgainPressed() = viewModelScope.launch {
        val result = uiState.value.result
        if (result == null) {
            _eventFlow.emit(SpreadResultUiEvent.ShowError("Result not found"))
        } else {
            val isDeleted = repository.deleteResult(result)
            if (isDeleted) {
                _eventFlow.emit(SpreadResultUiEvent.NavigateToDrawScreen)
            } else {
                _eventFlow.emit(SpreadResultUiEvent.ShowError("Failed to delete result"))
            }
        }
    }
}