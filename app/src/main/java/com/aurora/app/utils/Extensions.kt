package com.aurora.app.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

fun Throwable.logNonFatal() {
    Firebase.crashlytics.recordException(this)
}

fun Context.showToast(message: String) =  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()