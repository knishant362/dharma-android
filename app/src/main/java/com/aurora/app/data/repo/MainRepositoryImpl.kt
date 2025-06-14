package com.aurora.app.data.repo

import com.aurora.app.data.local.StorageManager
import com.aurora.app.data.model.SpreadResult
import com.aurora.app.data.model.User
import com.aurora.app.data.remote.api.ApiService
import com.aurora.app.data.remote.request.ImageUploadRequest
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.utils.ResponseState
import com.aurora.app.utils.safeApiCall
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val storageManager: StorageManager,
) : MainRepository {

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

    override suspend fun getHomepageData(): ResponseState<String> =
        safeApiCall(
            call = { apiService.fetchHomepageData() },
            success = { it }
        )

    override suspend fun getWallpapers(page: Int): ResponseState<String> =
        safeApiCall(
            call = { apiService.fetchWallpapers(page) },
            success = { it }
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