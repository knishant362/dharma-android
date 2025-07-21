package com.aurora.app.data.model

import android.os.Parcelable
import com.aurora.app.BuildConfig
import com.aurora.app.data.remote.response.WallpaperItem
import kotlinx.parcelize.Parcelize

data class WallpaperDataDto(
    val page: Int,
    val perPage: Int,
    val totalPages: Int,
    val wallpapers: List<WallpaperDto>
)

@Parcelize
data class WallpaperDto(
    val id: String,
    val collectionId: String,
    val collectionName: String,
    val albumId: String?,
    val title: String,
    val imageUrl: String,
    val resolution: String,
    val thumbnailUrl: String,
    val updatedDate: String,
    val createdDate: String,
) : Parcelable{
    companion object {
        // Convert WallpaperEntity to WallpaperDto
        fun fromEntity(entity: WallpaperItem): WallpaperDto {
            return WallpaperDto(
                collectionId = entity.collectionId,
                collectionName = entity.collectionName,
                createdDate = entity.created,
                id = entity.id,
                imageUrl = "${BuildConfig.BASE_URL}/api/files/${entity.collectionId}/${entity.id}/${entity.image_file}",
                resolution = entity.resolution,
                thumbnailUrl = entity.image_file,
                title = entity.title,
                albumId = entity.album_id,
                updatedDate = entity.updated,
            )
        }
    }
}
