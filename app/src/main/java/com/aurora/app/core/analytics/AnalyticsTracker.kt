package com.aurora.app.core.analytics

interface AnalyticsTracker {
    fun logEvent(eventName: String, params: Map<String, String>? = null)
}