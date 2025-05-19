package com.aurora.app.ui.screens.explore

import com.aurora.app.domain.model.explore.ExploreItem

data class ExploreUiState(
    val isLoading: Boolean = true,
    val errorMessages: String = "",
    val title: String = "",
    val subtitle: String = "",
    val exploreItems: List<ExploreItem> = emptyList(),
)