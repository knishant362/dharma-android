package com.aurora.app.ui.screens.dasbboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.R
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.domain.model.dashboard.TarotOption
import com.aurora.app.domain.repo.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        fetchHomepageData()
    }

    private fun fetchHomepageData() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                isLoading = false,
                featuredItems = getFeaturedData(),
                tarotOptions = getTarotSections()
            )
        }
    }

    private fun getFeaturedData(): List<Featured> {
        val featuredData = listOf(
            Featured(0, "YOUR TAROT READING\nFOR THE TODAY", "DRAW CARDS"),
            Featured(1, "CARD OF THE DAY", "DRAW NOW"),
            Featured(2, "HOW ARE YOU FEELING TODAY", "FIND OUT"),
        )
        return featuredData
    }

    private fun getTarotSections(): List<TarotOption> {
        val tarotOptions = listOf(
            TarotOption(0, "TODAY'S CARD", R.drawable.ic_tarot_card_one),
            TarotOption(1, "RELATIONSHIP", R.drawable.ic_tarot_love),
            TarotOption(3, "YES OR NO", R.drawable.ic_tarot_icon),
            TarotOption(4, "RELATIONSHIP", R.drawable.ic_tarot_three)
        )
        return tarotOptions
    }

}