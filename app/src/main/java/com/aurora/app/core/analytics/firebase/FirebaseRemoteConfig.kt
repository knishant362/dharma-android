package com.aurora.app.core.analytics.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object FirebaseRemoteConfig {
    internal fun init() {
        GlobalScope.launch {
            try {
                getRemoteConfig()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getRemoteConfig(): FirebaseRemoteConfig {
        val config = Firebase.remoteConfig
        val configSetting = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        config.setConfigSettingsAsync(configSetting)
        config.fetchAndActivate().await()
        config.fetch().addOnSuccessListener {
            config.fetchAndActivate();
        }
        return config
    }
}