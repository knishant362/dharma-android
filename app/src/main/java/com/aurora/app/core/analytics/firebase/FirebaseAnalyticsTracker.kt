package com.aurora.app.core.analytics.firebase

import androidx.core.os.bundleOf
import com.aurora.app.core.analytics.AnalyticsTracker
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseAnalyticsTracker : AnalyticsTracker {

    override fun logEvent(eventName: String, params: Map<String, String>?) {
        val bundle = bundleOf()
        params?.forEach { (key, value) -> bundle.putString(key, value) }
        Firebase.analytics.logEvent(eventName, bundle)
    }

}