package com.aurora.app.data.repo

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.aurora.app.data.remote.api.horoscope.HoroscopeService
import com.aurora.app.data.remote.response.HoroscopeData
import com.aurora.app.data.remote.response.ZodiacSign
import com.aurora.app.domain.repo.MediaRepository
import com.aurora.app.utils.ErrorType
import com.aurora.app.utils.ResponseState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val service: HoroscopeService
) : MediaRepository {

    private fun getVideoFileFromUrl(wallpaperId: String, extension: String): File {
        val hashedName = "$wallpaperId.$extension"
        return File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), hashedName)
    }

    override suspend fun downloadVideoIfNotExists(
        wallpaperId: String,
        extension: String,
        url: String
    ): ResponseState<File> = withContext(Dispatchers.IO) {
        val file = getVideoFileFromUrl(wallpaperId, extension)

        if (file.exists()) {
            return@withContext ResponseState.Success(file)
        }

        try {
            // Check URL validity
            val uri = Uri.parse(url)
            if (uri.scheme.isNullOrEmpty() || uri.host.isNullOrEmpty()) {
                return@withContext ResponseState.Error(errorType = ErrorType.NoInternet)
            }

            // Setup connection
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext ResponseState.Error(errorType = ErrorType.NoInternet)
            }

            val input = BufferedInputStream(connection.inputStream)
            val output = FileOutputStream(file)

            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var count: Int

            while (input.read(buffer).also { count = it } != -1) {
                output.write(buffer, 0, count)
            }

            output.flush()
            output.close()
            input.close()

            return@withContext ResponseState.Success(file)

        } catch (e: SocketTimeoutException) {
            file.delete()
            return@withContext ResponseState.Error(errorType = ErrorType.NoInternet)

        } catch (e: IOException) {
            file.delete()
            return@withContext ResponseState.Error(errorType = ErrorType.NoInternet)

        } catch (e: Exception) {
            file.delete()
            return@withContext ResponseState.Error(errorType = ErrorType.Unknown)
        }
    }

    override suspend fun getAllZodiacSigns(): ResponseState<List<ZodiacSign>> {
        try {
            val zodiacResult = service.getAllZodiacSigns()
            val data = zodiacResult.fold(
                onSuccess = { signs ->
                    Timber.e("Found ${signs.size} zodiac signs:")
                    ResponseState.Success(signs)
                },
                onFailure = { error ->
                    Timber.e("Error getting zodiac signs: ${error.message}")
                    ResponseState.Error(
                        errorType = ErrorType.Unknown,
                        message = error.message ?: "Unknown error"
                    )
                }
            )
            return data
        } catch (e: Exception) {
            Timber.e("Unexpected error: ${e.message}")
            return ResponseState.Error(
                errorType = ErrorType.Unknown,
                message = e.message ?: "Unknown error"
            )
        }
    }

    override suspend fun getHoroscopeForSign(zodiacSign: String): ResponseState<HoroscopeData> {
        return try {
            val horoscopeResult = service.getHoroscopeForSign(zodiacSign)
            horoscopeResult.fold(
                onSuccess = { json ->
                    Timber.e("Successfully extracted $zodiacSign horoscope")
                    ResponseState.Success(json)
                },
                onFailure = { error ->
                    Timber.e("Error extracting horoscope: ${error.message}")
                    ResponseState.Error(
                        errorType = ErrorType.Unknown,
                        message = error.message ?: "Unknown error"
                    )
                }
            )
        } catch (e: Exception) {
            Timber.e("Unexpected error: ${e.message}")
            ResponseState.Error(
                errorType = ErrorType.Unknown,
                message = e.message ?: "Unknown error"
            )
        }
    }
}