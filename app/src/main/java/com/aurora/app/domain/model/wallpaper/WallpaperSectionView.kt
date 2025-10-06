package com.aurora.app.domain.model.wallpaper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WallpaperSectionView(
    val id: String,
    val title: String,
    val wallpapers: List<WallpaperDto>
): Parcelable
