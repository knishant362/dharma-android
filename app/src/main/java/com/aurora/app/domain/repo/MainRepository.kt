package com.aurora.app.domain.repo

import com.aurora.app.data.model.SpreadResult
import com.aurora.app.data.remote.request.ImageUploadRequest
import com.aurora.app.utils.ResponseState

interface MainRepository {

    suspend fun getHomepageData(): ResponseState<String>

    suspend fun getWallpapers(page: Int): ResponseState<String>

    suspend fun uploadWallpaper(request: ImageUploadRequest): ResponseState<String>

    suspend fun saveSpread(spreadDetailId: String, selectedCardIds: List<String>)

    suspend fun getSavedSpreads(): List<SpreadResult>

    suspend fun getSpreadResultBySpreadId(spreadId: String): List<SpreadResult>

    suspend fun deleteResult(result: SpreadResult) : Boolean
}