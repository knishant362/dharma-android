package com.aurora.app.utils

sealed class ResponseState<T>(
    val data: T? = null,
    val message: String? = ErrorType.Unknown.errorMessage,
    val errorType: ErrorType = ErrorType.Unknown
) {
    class Loading<T> : ResponseState<T>()
    class Success<T>(data: T?, message: String? = null) : ResponseState<T>(data, message)
    class Error<T>(data: T? = null, errorType: ErrorType = ErrorType.Unknown, message: String? = ErrorType.Unknown.errorMessage) :
        ResponseState<T>(data, message, errorType)
}