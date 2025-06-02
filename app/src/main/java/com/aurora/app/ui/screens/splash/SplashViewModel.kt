package com.aurora.app.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.data.local.StorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val storageManager: StorageManager
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        checkUserDataAndNavigate()
    }

    private fun checkUserDataAndNavigate() = viewModelScope.launch {

        Timber.e("UserData -> name: ${storageManager.getName()}, dob: ${storageManager.getDateOfBirth()}, gender: ${storageManager.getGender()}")


        viewModelScope.launch {
            val name = storageManager.getName()
            delay(1000)
            if (name.isBlank()) {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToOnboarding)
            } else {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToDashboard)
            }
        }
    }
}
