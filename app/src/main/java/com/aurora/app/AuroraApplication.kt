package com.aurora.app

import android.app.Application
import com.aurora.app.core.analytics.firebase.FirebaseApp
import com.aurora.app.utils.Constants.ONESIGNAL_APP_ID
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@HiltAndroidApp
class AuroraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        FirebaseApp.init()
        subscribeUserFirebase()
        initOneSignal()

    }

    private fun subscribeUserFirebase() {
        GlobalScope.launch {
            runCatching {
                Firebase.messaging.subscribeToTopic("weather").await()
            }
        }
    }

    private fun initOneSignal() {
        OneSignal.initWithContext(this@AuroraApplication, ONESIGNAL_APP_ID)
    }

}