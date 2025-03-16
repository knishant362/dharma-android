package com.aurora.app.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

// Function to safely make API calls and handle errors using timber and ResponseState class
suspend fun <T, R> safeApiCall(
    successMessage: String = "",
    errorMessage: String? = null,
    call: suspend () -> Response<T>,
    success: (data: T) -> R // Modify data of type T to your desired type R (DTO or similar)
): ResponseState<R> = withContext(Dispatchers.IO) {
    return@withContext try {
        val response = call()
        if (response.isSuccessful) {
            response.body()?.let { body ->
                // Map the body using the success block
                val mappedData = success(body)
                ResponseState.Success(mappedData, successMessage)
            } ?: run {
                ResponseState.Error(message = errorMessage ?: "DATA NULL")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Timber.tag("safeApiCall").d("safeApiCall else: $errorBody")
            ResponseState.Error(message = errorMessage ?: response.message())
        }
    } catch (e: IOException) {
        Timber.e(e)
        ResponseState.Error(errorType = ErrorType.NoInternet, message = errorMessage ?: e.message.toString())
    } catch (e: Exception) {
        Timber.e(e)
        ResponseState.Error(errorType = ErrorType.Unknown, message = errorMessage ?: e.message.toString())
    }
}


sealed class ErrorType(open val title: String, open val errorMessage: String) {
    data object NoInternet : ErrorType(
        "No Internet",
        "Looks like you don't have an active internet connection"
    )

    data object Unknown : ErrorType("Unknown Error", "Oops something went wrong")
}