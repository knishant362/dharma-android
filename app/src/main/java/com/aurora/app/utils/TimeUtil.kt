package com.aurora.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtil {

    private val monthDayFormat = SimpleDateFormat("MMMM d", Locale.getDefault())

    fun formatTimestampToMonthDay(timestamp: Long): String {
        return try {
            val date = Date(timestamp)
            monthDayFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}
