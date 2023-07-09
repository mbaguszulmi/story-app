package co.mbznetwork.storyapp.util

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    fun formatDate(date: Date, format: String): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(date)
    }

    fun formatDateTime(date: Date): String {
        return SimpleDateFormat.getDateTimeInstance().format(date)
    }

}