package com.aurora.app.core.analytics.firebase

import android.os.Bundle
import com.aurora.app.BuildConfig
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object Analytics {
    private val analytics = Firebase.analytics

    fun logEvent(name: String, vararg params: Pair<String, Any?>) {
        val bundle = Bundle().apply {
            // Default params for every event
            putString("timestamp", System.currentTimeMillis().toString())
            putString("app_version", BuildConfig.VERSION_NAME)

            // Custom params
            params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putString(key, value.toString()) // Firebase doesn't support Boolean directly
                    null -> putString(key, "null")
                }
            }
        }
        analytics.logEvent(name, bundle)
    }
}
