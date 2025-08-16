package com.aurora.app.domain.repo

import com.aurora.app.data.remote.response.HoroscopeData
import com.aurora.app.data.remote.response.ZodiacSign
import com.aurora.app.utils.ResponseState
import java.io.File

interface MediaRepository {

    suspend fun downloadVideoIfNotExists(
        wallpaperId: String,
        extension: String,
        url: String
    ): ResponseState<File>

    suspend fun getAllZodiacSigns(): ResponseState<List<ZodiacSign>>

    suspend fun getHoroscopeForSign(zodiacSign: String): ResponseState<HoroscopeData>
}