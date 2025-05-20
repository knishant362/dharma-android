package com.aurora.app.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.model.spread.toSpreadDetailDTO
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.domain.repo.TarotRepository
import com.aurora.app.utils.TimeUtil.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val repository: TarotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        setupExploreData()
    }

    fun setupExploreData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val results = mainRepository.getSavedSpreads()
                val todaySpreadResults = results.filter { isToday(it.createdAt) }
                val todaySpreadResultsMap = todaySpreadResults.associateBy { it.spreadDetailId }
                val spreads = repository.getExploreSpreads().map { it.toSpreadDetailDTO(todaySpreadResultsMap[it.id] )}
                _uiState.value = ExploreUiState(
                    isLoading = false,
                    title = "Choose what you would\nlike to explore",
                    subtitle = "What's the nature of your concern, UserName?",
                    todayResults = todaySpreadResults,
                    spreadResults = results,
                    spreads = spreads
                )
            } catch (e: Exception) {
                _uiState.value =
                    ExploreUiState(errorMessages = e.message ?: "Error loading spreads")
            }
        }
    }

}