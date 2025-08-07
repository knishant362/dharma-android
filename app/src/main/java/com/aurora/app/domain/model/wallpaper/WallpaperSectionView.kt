package com.aurora.app.domain.model.wallpaper

data class WallpaperSectionView(
    val id: String,
    val title: String,
    val wallpapers: List<WallpaperDto>
)
