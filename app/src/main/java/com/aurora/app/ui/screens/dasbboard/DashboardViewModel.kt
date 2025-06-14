package com.aurora.app.ui.screens.dasbboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.domain.model.spread.toSpreadDetailDTO
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.domain.repo.TarotRepository
import com.aurora.app.utils.TimeUtil
import com.aurora.app.utils.TimeUtil.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val tarotRepository: TarotRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        setupDashboard()
    }

    private fun getFeaturedData(): List<Featured> {
        val date = TimeUtil.getTodayFormatted()
        val featuredData = listOf(
            Featured(0, date, "YOUR TAROT READING\nFOR THE TODAY", "DRAW CARDS"),
            Featured(1, date, "CARD OF THE DAY", "DRAW NOW"),
            Featured(2, date, "WHAT'S YOUR ENERGY\nTODAY?", "FIND OUT"),
        )
        return featuredData
    }

    private fun setupDashboard() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val results = mainRepository.getSavedSpreads()
                val todaySpreadResults = results.filter { isToday(it.createdAt) }
                val todaySpreadResultsMap = todaySpreadResults.associateBy { it.spreadDetailId }
                val spreads = tarotRepository.getDashboardSpreads().mapIndexed { index, spread ->
                    spread.toSpreadDetailDTO(index, todaySpreadResultsMap[spread.id])
                }
                val userProfile = mainRepository.getUserProfile()
                _uiState.value = DashboardUiState(
                    isLoading = false,
                    featuredItems = getFeaturedData(),
                    todayResults = todaySpreadResults,
                    spreadResults = results,
                    spreads = spreads,
                    user = userProfile
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState(errorMessages = e.message ?: "Error loading spreads")
            }
        }
    }

}