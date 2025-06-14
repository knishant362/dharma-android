package com.aurora.app.ui.screens.profile


sealed class ProfileEvent {
    data class ShowToast(val message: String) : ProfileEvent()
}