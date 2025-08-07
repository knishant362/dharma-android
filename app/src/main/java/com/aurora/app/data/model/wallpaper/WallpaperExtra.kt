package com.aurora.app.data.model.wallpaper

import com.google.gson.annotations.SerializedName

data class WallpaperExtra(
    @SerializedName("daily_title")
    val dailyTitle: String,
    @SerializedName("god_title")
    val godTitle: String,
    @SerializedName("lockscreen_msg")
    val lockscreenMsg: String,
    @SerializedName("premium_enabled")
    val premiumEnabled: Boolean,
    @SerializedName("sections")
    val sections: List<WallpaperSection>,
    @SerializedName("set_lockscreen")
    val setLockscreen: String,
    @SerializedName("set_wallpaper")
    val setWallpaper: String,
    @SerializedName("wallpaper_msg")
    val wallpaperMsg: String
)

data class WallpaperSection(
    @SerializedName("data")
    val data: String,
    @SerializedName("stype")
    val stype: String,
    @SerializedName("title")
    val title: String
)