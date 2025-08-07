package com.aurora.app.data.repo

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.aurora.app.domain.repo.MediaRepository
import com.aurora.app.utils.ErrorType
import com.aurora.app.utils.ResponseState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
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
}