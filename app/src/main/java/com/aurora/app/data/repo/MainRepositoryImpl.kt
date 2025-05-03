package com.aurora.app.data.repo

import com.aurora.app.data.local.SpreadStorageManager
import com.aurora.app.data.model.SpreadResult
import com.aurora.app.data.remote.api.ApiService
import com.aurora.app.data.remote.request.ImageUploadRequest
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.utils.ResponseState
import com.aurora.app.utils.safeApiCall
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val spreadStorageManager: SpreadStorageManager
) : MainRepository {

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
        spreadStorageManager.saveSpread(spreadDetailId, selectedCardIds)
    }

    override suspend fun getSavedSpreads(): List<SpreadResult> {
        return spreadStorageManager.getSavedSpreads()
    }

    override suspend fun getSpreadResultBySpreadId(spreadId: String): List<SpreadResult> {
        return spreadStorageManager.getSpreadsBySpreadId(spreadId)
    }

    override suspend fun deleteResult(result: SpreadResult): Boolean {
        return spreadStorageManager.deleteResult(result)
    }

}