package com.aurora.app.data.remote.response

import com.aurora.app.data.model.WallpaperDataDto
import com.aurora.app.data.model.WallpaperDto
import com.google.gson.annotations.SerializedName

data class WallpapersResponse(
    @SerializedName("items")
    val wallpapers: List<WallpaperItem>,
    val page: Int,
    val perPage: Int,
    val totalItems: Int,
    val totalPages: Int
) {
    fun toWallpaperDataDto(): WallpaperDataDto {
        return WallpaperDataDto(
            page = page,
            perPage = perPage,
            totalPages = totalPages,
            wallpapers = wallpapers.map { WallpaperDto.fromEntity(it) }
        )
    }
}

data class WallpaperItem(
    val collectionId: String,
    val collectionName: String,
    val created: String,
    val id: String,
    val image_file: String,
    val resolution: String,
    val title: String,
    val updated: String,
    val album_id: String?
)