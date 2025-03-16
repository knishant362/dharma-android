package com.aurora.app.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

// Function to safely call a function and handle errors using timber logging and ResponseState class
fun <T> runSafe(call: () -> T): ResponseState<T> = try {
    val result = call()
    ResponseState.Success(result)
} catch (e: Exception) {
    Timber.d(e.message.toString())
    ResponseState.Error(message = e.message.toString())
}

// Function to call runSafe function asynchronously
suspend fun <T> runSafeAsync(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    call: () -> T
): ResponseState<T> = withContext(dispatcher) {
    return@withContext runSafe(call)
}