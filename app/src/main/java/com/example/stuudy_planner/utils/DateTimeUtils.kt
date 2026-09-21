package com.example.stuudy_planner.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun getTodayDate(): String {
        return dbDateFormat.format(Date())
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 4..11 -> "Good Morning, Student!"
            hour in 12..16 -> "Good Afternoon, Student!"
            hour in 17..21 -> "Good Evening, Student!"
            else -> "Good Night, Student!"
        }
    }

    fun formatDisplayDate(dateStr: String): String {
        return try {
            val date = dbDateFormat.parse(dateStr) ?: return dateStr
            val todayStr = getTodayDate()
            if (dateStr == todayStr) {
                "Today, ${SimpleDateFormat("MMM d", Locale.getDefault()).format(date)}"
            } else {
                displayDateFormat.format(date)
            }
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatTime(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        return timeFormat.format(calendar.time)
    }

    fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        return dbDateFormat.format(calendar.time)
    }

    fun getReminderTimestamp(dateStr: String, timeStr: String): Long {
        return try {
            val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
            val date = dateTimeFormat.parse("$dateStr $timeStr")
            date?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
