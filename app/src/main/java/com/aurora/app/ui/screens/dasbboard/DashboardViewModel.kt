package com.aurora.app.ui.screens.dasbboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.utils.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
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
        Timber.tag("fetchHomepageData").d("fetchHomepageData")
        val result = mainRepository.getHomepageData()
        _uiState.update {
            when (result) {
                is ResponseState.Loading -> it
                is ResponseState.Success -> it.copy(
                    isLoading = false,
                )
                is ResponseState.Error -> it.copy(
                    isLoading = false,
                    errorMessages = result.errorType.errorMessage,
                )
            }
        }
    }


}