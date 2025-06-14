package com.aurora.app.ui.components.dialog

data class DialogUiState(
    val isVisible: Boolean = false,
    val title: String = "",
    val subtitle: String? = null,
    val dropdownOptions: List<String> = emptyList(),
    val positiveButtonText: String = "",
    val negativeButtonText: String? = null,
)