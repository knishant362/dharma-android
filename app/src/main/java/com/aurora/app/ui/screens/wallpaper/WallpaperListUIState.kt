package com.aurora.app.ui.screens.wallpaper

import com.aurora.app.domain.model.wallpaper.WallpaperSectionDto
import com.aurora.app.domain.model.wallpaper.WallpaperSectionView

data class WallpaperListUiState(
    val isLoading: Boolean = false,
    val errorMessages: String = "",
    val rawSections: List<WallpaperSectionDto> = emptyList(),
    val wallpaperSections: List<WallpaperSectionView> = emptyList()
)