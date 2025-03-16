package com.aurora.app.core.analytics.firebase

import androidx.core.os.bundleOf
import com.aurora.app.core.analytics.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object FirebaseEvent: AnalyticsTracker {

    override fun logEvent(eventName: String, params: Map<String, String>?) {
        val bundle = bundleOf()
        params?.forEach { (key, value) -> bundle.putString(key, value) }
        Firebase.analytics.logEvent(eventName, bundle)
    }

    fun sendEvent(eventName: String, param: Map<String, String>) {
        GlobalScope.launch {
            val bundle = bundleOf()
            param.forEach { (t, u) ->
                bundle.putString(t, u)
            }
            Firebase.analytics.logEvent(eventName, bundle)
        }
    }

    object EventParam {
        val itemId = FirebaseAnalytics.Param.ITEM_ID
        val content = FirebaseAnalytics.Param.CONTENT
        val action = "action"
        val error = "error_reason"
        val appName = "app_name"
    }
}