package com.aurora.app.core.analytics.firebase

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(DelicateCoroutinesApi::class)
object FirebaseApp {
    fun init() {
        GlobalScope.launch {
            FirebaseRemoteConfig.init()
        }
        GlobalScope.launch {
            try {
                Firebase.messaging.subscribeToTopic("users").await()
            } catch (e: Exception) {
            }
        }
    }

    fun recordNonFatal(throwable: Throwable) {
        GlobalScope.launch {
            Firebase.crashlytics.recordException(throwable)
        }
    }
}