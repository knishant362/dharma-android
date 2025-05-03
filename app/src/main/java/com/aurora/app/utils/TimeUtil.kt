package com.aurora.app.utils

import java.text.SimpleDateFormat
import java.util.Calendar
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

    fun isToday(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }
}
