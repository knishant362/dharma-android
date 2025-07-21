package com.aurora.app.utils

import android.content.Context
import android.widget.Toast
import com.aurora.app.BuildConfig
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

fun Throwable.logNonFatal() {
    Firebase.crashlytics.recordException(this)
}

fun Context.showToast(message: String) =  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun String.toThumb(): String {
    return "$this?thumb=100x300f"
}

fun String.toDownloadUrl(): String {
    return "${BuildConfig.BASE_URL}/${Constants.FILE_ENDPOINT}$this"
}
