package com.aurora.app.domain.repo

import com.aurora.app.utils.ResponseState
import java.io.File

interface MediaRepository {
    suspend fun downloadVideoIfNotExists(wallpaperId: String, extension: String, url: String): ResponseState<File>
}