package com.aurora.app.ui.screens.horoscope

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.data.remote.response.ZodiacSign
import com.aurora.app.domain.repo.MediaRepository
import com.aurora.app.utils.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoroscopeViewModel @Inject constructor(
    private val mainRepository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HoroscopeUiState())
    val uiState: StateFlow<HoroscopeUiState> = _uiState.asStateFlow()

    init {
        loadZodiacSigns()
    }

    private fun loadZodiacSigns() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = mainRepository.getAllZodiacSigns()
                when (response) {
                    is ResponseState.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is ResponseState.Success -> {
                        val signs = response.data ?: emptyList()
                        if (signs.isEmpty()) {
                            throw Exception("कोई राशि चिन्ह नहीं मिला")
                        }
                        _uiState.update { currentState ->
                            currentState.copy(
                                zodiacSigns = signs,
                                selectedZodiacSign = signs.firstOrNull { it.englishName == "cancer" },
                                isLoading = false
                            )
                        }
                        loadHoroscopeForCurrentSign()

                    }

                    is ResponseState.Error -> {
                        throw Exception(response.errorType.errorMessage)
                    }

                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "राशि चिन्ह लोड करने में त्रुटि: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectZodiacSign(zodiacSign: ZodiacSign) {
        _uiState.update {
            it.copy(selectedZodiacSign = zodiacSign, horoscopeData = null)
        }
        loadHoroscopeForCurrentSign()
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun refreshHoroscope() {
        loadHoroscopeForCurrentSign()
    }

    private fun loadHoroscopeForCurrentSign() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val currentSign = _uiState.value.selectedZodiacSign ?: return
        viewModelScope.launch {
            val response = mainRepository.getHoroscopeForSign(currentSign.englishName)
            when (response) {
                is ResponseState.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }

                is ResponseState.Success -> {
                    val horoscope = response.data
                    delay(500)
                    _uiState.update { it.copy(horoscopeData = horoscope, isLoading = false) }
                }

                is ResponseState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "राशिफल लोड करने में त्रुटि: ${response.message}"
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}