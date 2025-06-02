package com.aurora.app.ui.screens.splash

sealed class SplashNavigationEvent {
    data object NavigateToDashboard : SplashNavigationEvent()
    data object NavigateToOnboarding : SplashNavigationEvent()
}