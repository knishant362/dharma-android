package com.aurora.app.domain.repo

import com.aurora.app.data.local.database.entity.PostEntity
import com.aurora.app.data.model.SpreadResult
import com.aurora.app.data.model.User
import com.aurora.app.data.model.WallpaperDataDto
import com.aurora.app.data.model.WallpaperDto
import com.aurora.app.data.model.WorkDto
import com.aurora.app.data.model.work.WorkModel
import com.aurora.app.data.remote.request.ImageUploadRequest
import com.aurora.app.domain.model.ReaderStyle
import com.aurora.app.utils.ResponseState

interface MainRepository {

    suspend fun fetchReaderStyle(): ReaderStyle

    suspend fun setReaderStyle(readerStyle: ReaderStyle)

    suspend fun getUserProfile(): User

    suspend fun fetchWorks(): ResponseState<List<WorkDto>>

    suspend fun getWallpapers(page: Int): ResponseState<WallpaperDataDto>

    suspend fun getAlbumWallpapers(albumId: String): ResponseState<List<WallpaperDto>>

    suspend fun uploadWallpaper(request: ImageUploadRequest): ResponseState<String>

    suspend fun saveSpread(spreadDetailId: String, selectedCardIds: List<String>)

    suspend fun getSavedSpreads(): List<SpreadResult>

    suspend fun getSpreadResultBySpreadId(spreadId: String): List<SpreadResult>

    suspend fun deleteResult(result: SpreadResult) : Boolean

    suspend fun saveUserProfile(
        name: String,
        gender: String,
        dateOfBirth: String,
        relationshipStatus: String,
        occupation: String
    )

    suspend fun getWorkDetails(workDto: WorkDto): ResponseState<WorkModel?>

    suspend fun getAllPosts(): List<PostEntity>

    suspend fun getPosts(id: String): List<PostEntity>


}