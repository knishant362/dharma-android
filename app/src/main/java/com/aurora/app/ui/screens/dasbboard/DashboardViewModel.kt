package com.aurora.app.ui.screens.dasbboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.domain.model.dashboard.WorkSection
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.utils.ResponseState
import com.aurora.app.utils.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {


    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        setupDashboard()
        fetchWorks()
    }

    private fun fetchWorks() {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = mainRepository.getAllPosts()
            Timber.e("Fetched posts: ${posts.size}")

            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val response = mainRepository.fetchWorks()
                Timber.e("Fetched works response: $response")
                when(response) {
                    is ResponseState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessages = response.message ?: "Error fetching works"
                        )
                    }
                    is ResponseState.Loading -> {}
                    is ResponseState.Success -> {
                        val works = response.data ?: emptyList()
                        Timber.e("Fetched works: ${works.size}")
                        val workSections = works.groupBy { it.category }
                            .map { (categoryName, works) ->
                                WorkSection(
                                    id = categoryName,
                                    categoryName = categoryName,
                                    works = works
                                )
                            }
                        _uiState.value = _uiState.value.copy(works = works, workSections = workSections, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessages = e.message ?: "Error fetching works")
            }
        }
    }

    private fun getFeaturedDataHindi(): List<Featured> {
        val date = TimeUtil.getTodayFormatted()
        val featuredData = listOf(
            Featured(0, date, "आज का आध्यात्मिक संदेश", "अभी पढ़ें"),
            Featured(1, date, "दिन का श्लोक", "अभी देखें"),
            Featured(2, date, "आपकी ऊर्जा आज कैसी है?", "जानिए"),
        )
        return featuredData
    }

    private fun setupDashboard() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userProfile = mainRepository.getUserProfile()
                _uiState.value = DashboardUiState(
                    featuredItems = getFeaturedDataHindi(),
                    user = userProfile
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState(errorMessages = e.message ?: "Error loading spreads")
            }
        }
    }

}