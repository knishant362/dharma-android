package com.aurora.app.domain.model.wallpaper

import com.aurora.app.data.model.wallpaper.WallpaperExtra
import com.aurora.app.data.model.wallpaper.WallpaperSection

data class WallpaperExtraDto(
    val dailyTitle: String,
    val godTitle: String,
    val lockscreenMsg: String,
    val premiumEnabled: Boolean,
    val sections: List<WallpaperSectionDto>,
    val setLockscreen: String,
    val setWallpaper: String,
    val wallpaperMsg: String
)

data class WallpaperSectionDto(
    val data: String,
    val stype: String,
    val title: String
)

fun WallpaperExtra.toDto(): WallpaperExtraDto {
    return WallpaperExtraDto(
        dailyTitle = dailyTitle,
        godTitle = godTitle,
        lockscreenMsg = lockscreenMsg,
        premiumEnabled = premiumEnabled,
        sections = sections.map { it.toDto() },
        setLockscreen = setLockscreen,
        setWallpaper = setWallpaper,
        wallpaperMsg = wallpaperMsg
    )
}

fun WallpaperSection.toDto(): WallpaperSectionDto {
    return WallpaperSectionDto(
        data = data,
        stype = stype,
        title = title
    )
}