package com.fawadini.eznotes.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatter {

    fun formatNoteDate(timestamp: Long): String {
        val now = Calendar.getInstance()
        val noteCal = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isSameDay = now.get(Calendar.YEAR) == noteCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == noteCal.get(Calendar.DAY_OF_YEAR)

        val isYesterday = now.get(Calendar.YEAR) == noteCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - noteCal.get(Calendar.DAY_OF_YEAR) == 1

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        return when {
            isSameDay -> "Today, ${timeFormat.format(Date(timestamp))}"
            isYesterday -> "Yesterday, ${timeFormat.format(Date(timestamp))}"
            now.get(Calendar.YEAR) == noteCal.get(Calendar.YEAR) -> {
                val monthDayFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                monthDayFormat.format(Date(timestamp))
            }
            else -> {
                val fullFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                fullFormat.format(Date(timestamp))
            }
        }
    }
}
