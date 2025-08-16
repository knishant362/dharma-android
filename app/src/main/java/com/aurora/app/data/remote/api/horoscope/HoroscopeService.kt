package com.aurora.app.data.remote.api.horoscope

import com.aurora.app.data.remote.response.HoroscopeData
import com.aurora.app.data.remote.response.ZodiacSign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class HoroscopeService {
    private val extractor = HoroscopeExtractor()

    suspend fun getHoroscopeForSign(zodiacSign: String): Result<HoroscopeData> {
        val baseUrl = "https://www.ganeshaspeaks.com/hindi/horoscopes/daily-horoscope"
        val url = "$baseUrl/${zodiacSign.lowercase()}/"
        Timber.e("getHoroscopeForSign: $url")
        return extractor.extractHoroscopeData(url)
    }

    suspend fun getAllZodiacSigns(): Result<List<ZodiacSign>> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = "https://www.ganeshaspeaks.com/hindi/horoscopes/daily-horoscope/"
            val zodiacSigns = extractor.extractZodiacSigns(baseUrl)
            Result.success(zodiacSigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}