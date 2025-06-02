package com.aurora.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.data.local.StorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val storageManager: StorageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateToDashboard = MutableSharedFlow<Unit>()
    val navigateToDashboard: SharedFlow<Unit> = _navigateToDashboard.asSharedFlow()

    fun onNameEntered(newName: String) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(name = newName)
        storageManager.setName(newName)
        goToNextStep()
    }

    fun onDateOfBirthEntered(date: String) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(dateOfBirth = date)
        storageManager.setDateOfBirth(date)
        goToNextStep()
    }

    fun onGenderEntered(gender: String) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(gender = gender)
        storageManager.setGender(gender)
        goToNextStep()
    }

    fun onNotificationPermissionGranted() {
        goToNextStep()
    }

    fun onFinish() {
        viewModelScope.launch {
            _navigateToDashboard.emit(Unit)
        }
    }

    private fun goToNextStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep < _uiState.value.totalSteps - 1) {
            _uiState.value = _uiState.value.copy(currentStep = currentStep + 1)
        }
    }
}
