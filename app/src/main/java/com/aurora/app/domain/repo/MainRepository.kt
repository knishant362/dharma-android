package com.aurora.app.domain.repo

import com.aurora.app.data.remote.request.ImageUploadRequest
import com.aurora.app.utils.ResponseState

interface MainRepository {

    suspend fun getHomepageData(): ResponseState<String>

    suspend fun getWallpapers(page: Int): ResponseState<String>

    suspend fun uploadWallpaper(request: ImageUploadRequest): ResponseState<String>


}