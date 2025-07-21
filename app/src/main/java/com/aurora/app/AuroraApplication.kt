package com.aurora.app

import android.app.Application
import com.aurora.app.utils.Constants.ONESIGNAL_APP_ID
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.FileOutputStream
import java.io.IOException

@HiltAndroidApp
class AuroraApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        subscribeUserFirebase()
        initOneSignal()
        checkAndCopyPrepopulatedDatabase()

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

    fun checkAndCopyPrepopulatedDatabase() {
        val dbName = "dharma_database.db"
        val assetPath = dbName
        val dbPath = applicationContext.getDatabasePath(dbName)
        if (!dbPath.exists()) {
            try {
                dbPath.parentFile?.mkdirs()

                applicationContext.assets.open(assetPath).use { input ->
                    FileOutputStream(dbPath).use { output ->
                        input.copyTo(output)
                    }
                }

                Timber.d("DB_COPY Pre-populated DB copied to: ${dbPath.absolutePath}")
            } catch (e: IOException) {
                Timber.e("DB_COPY Error copying pre-populated DB: ${e.localizedMessage}", e)
            }
        } else {
            Timber.d("DB_COPY Database already exists at: ${dbPath.absolutePath}")
        }
    }


}