package com.aurora.app.utils

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

fun Throwable.logNonFatal() {
    Firebase.crashlytics.recordException(this)
}