package com.aurora.app.ui.screens.dasbboard

import com.aurora.app.data.model.User
import com.aurora.app.data.model.WorkDto
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.domain.model.dashboard.WorkSection

data class DashboardUiState(
    val isLoading: Boolean = false,
    val errorMessages: String = "",
    val featuredItems: List<Featured> = emptyList(),
    val works: List<WorkDto> = emptyList(),
    val workSections: List<WorkSection> = emptyList(),
    val user: User? = null,
    val categories: List<CategoryItem> = emptyList(),

    val remoteVersion: Int = 1,
    val showUpgradeDialog: Boolean = false
)