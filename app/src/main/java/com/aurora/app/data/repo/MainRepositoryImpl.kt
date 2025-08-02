package com.aurora.app.data.repo

import android.content.Context
import com.aurora.app.BuildConfig
import com.aurora.app.data.local.database.dao.AppDao
import com.aurora.app.data.local.database.entity.PostEntity
import com.aurora.app.data.local.storage.StorageManager
import com.aurora.app.data.model.SpreadResult
import com.aurora.app.data.model.User
import com.aurora.app.data.model.WallpaperDataDto
import com.aurora.app.data.model.WallpaperDto
import com.aurora.app.data.model.WorkDto
import com.aurora.app.data.model.toReaderStyleModel
import com.aurora.app.data.model.toWorkDto
import com.aurora.app.data.model.work.WorkModel
import com.aurora.app.data.remote.api.ApiService
import com.aurora.app.data.remote.request.ImageUploadRequest
import com.aurora.app.domain.model.ReaderStyle
import com.aurora.app.domain.model.toReaderStyle
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.utils.Constants
import com.aurora.app.utils.ResponseState
import com.aurora.app.utils.safeApiCall
import com.google.gson.Gson
import java.io.File
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val storageManager: StorageManager,
    private val appDao: AppDao
) : MainRepository {

    override suspend fun fetchReaderStyle(): ReaderStyle {
        return storageManager.getReaderStyle()?.toReaderStyle() ?: ReaderStyle.Default
    }

    override suspend fun setReaderStyle(readerStyle: ReaderStyle) {
        storageManager.setReaderStyle(readerStyle.toReaderStyleModel())
    }

    override suspend fun getUserProfile(): User {
        return User(
            name = storageManager.getName(),
            gender = storageManager.getGender(),
            dateOfBirth = storageManager.getDateOfBirth(),
            relationshipStatus = storageManager.getRelationshipStatus(),
            occupation = storageManager.getOccupation()
        )
    }

    override suspend fun saveUserProfile(
        name: String,
        gender: String,
        dateOfBirth: String,
        relationshipStatus: String,
        occupation: String
    ) {
        storageManager.setName(name)
        storageManager.setGender(gender)
        storageManager.setDateOfBirth(dateOfBirth)
        storageManager.setRelationshipStatus(relationshipStatus)
        storageManager.setOccupation(occupation)
    }
    val gson = Gson()

    override suspend fun getWorkDetails(workDto: WorkDto): ResponseState<WorkModel?> {
        val folder = File(context.filesDir, Constants.WORK_DIRECTORY)
        val filename = "${workDto.id}.json"
        val workFile = File(folder, filename)
        if (workFile.exists()) {
            val workModel = gson.fromJson(workFile.readText(), WorkModel::class.java)
            return ResponseState.Success(workModel)
        } else {
            val fileUrl = "${BuildConfig.BASE_URL}/${Constants.FILE_ENDPOINT}/${workDto.jsonFile}"
            val response = apiService.fetchWorkFile(fileUrl)
            if (response.isSuccessful) {
                val json = response.body()
                if (json != null) {
                    folder.mkdirs() // Create directory if it doesn't exist
                    workFile.writeText(gson.toJson(json))
                    val data = gson.fromJson(json, WorkModel::class.java)
                    return ResponseState.Success(message = "File download successfully", data = data)
                } else {
                    return ResponseState.Error(message = "Work model is null")
                }
            } else {
                return ResponseState.Error(message = "Failed to fetch work file: ${response.message()}")
            }
        }
    }

    override suspend fun getAllPosts(): List<PostEntity> {
        return appDao.getAllPosts()
    }

    override suspend fun getPosts(id: String): List<PostEntity> {
        return appDao.getPostsById(id)
    }

    override suspend fun fetchWorks(): ResponseState<List<WorkDto>> =
        safeApiCall(
            call = { apiService.fetchWorks() },
            success = { it.items.map { work -> work.toWorkDto() } }
        )

    override suspend fun getWallpapers(page: Int): ResponseState<WallpaperDataDto> =
        safeApiCall(
            call = { apiService.fetchWallpapers(page) },
            success = { it.toWallpaperDataDto() }
        )

    override suspend fun getAlbumWallpapers(albumId: String): ResponseState<List<WallpaperDto>> =
        safeApiCall(
            call = { apiService.fetchAlbumWallpapers("(album_id='${albumId}')") },
            success = { it.wallpapers.map { item -> WallpaperDto.fromEntity(item) } }
        )

    override suspend fun uploadWallpaper(request: ImageUploadRequest): ResponseState<String> =
        safeApiCall(
            call = {
                apiService.uploadWallpaper(
                    imageFile = request.imageFilePart,
                    resolution = request.resolution,
                    title = request.title,
                    albumId = request.albumId
                )
            },
            success = { it }
        )


    override suspend fun saveSpread(spreadDetailId: String, selectedCardIds: List<String>) {
        storageManager.saveSpread(spreadDetailId, selectedCardIds)
    }

    override suspend fun getSavedSpreads(): List<SpreadResult> {
        return storageManager.getSavedSpreads()
    }

    override suspend fun getSpreadResultBySpreadId(spreadId: String): List<SpreadResult> {
        return storageManager.getSpreadsBySpreadId(spreadId)
    }

    override suspend fun deleteResult(result: SpreadResult): Boolean {
        return storageManager.deleteResult(result)
    }

}