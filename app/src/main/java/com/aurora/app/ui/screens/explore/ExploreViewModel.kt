package com.aurora.app.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.R
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.domain.model.dashboard.TarotOption
import com.aurora.app.domain.model.explore.ExploreItem
import com.aurora.app.domain.repo.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        fetchHomepageData()
    }

    private fun fetchHomepageData() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                isLoading = false,
                title = "Choose what you would\nlike to explore",
                subtitle = "What's the nature of your concern, UserName?",
                exploreItems = getExploreData()
            )
        }
    }

    private fun getExploreData(): List<ExploreItem> {
        val featuredData = listOf(
            ExploreItem(0, "My Love Life Developments", true),
            ExploreItem(1, "My Prosperity and Wellbeing", true),
            ExploreItem(2, "What Awaits Me Soon", true)
        )
        return featuredData
    }


}