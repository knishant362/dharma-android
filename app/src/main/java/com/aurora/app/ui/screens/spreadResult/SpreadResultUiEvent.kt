package com.aurora.app.ui.screens.spreadResult

sealed class SpreadResultUiEvent {
    data object NavigateToDrawScreen: SpreadResultUiEvent()
    data class ShowError(val message: String) : SpreadResultUiEvent()
}