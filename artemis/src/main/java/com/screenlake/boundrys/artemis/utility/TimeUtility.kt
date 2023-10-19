package com.screenlake.boundrys.artemis.utility

import java.text.SimpleDateFormat
import java.util.*

object TimeUtility {
    fun getFormattedHhMmSs(utcTimestamp: Long): String {
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val date = Date(utcTimestamp * 1000)
        return formatter.format(date)
    }

    fun getFormattedDay(utcTimestamp: Long): String {
        val formatter = SimpleDateFormat("E", Locale.getDefault())
        val date = Date(utcTimestamp * 1000)
        return formatter.format(date)
    }

    fun getFormattedDate(utcTimestamp: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date = Date(utcTimestamp * 1000)
        return formatter.format(date)
    }

    fun convertToEpochTime(utcString: String?): Long {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        if (utcString.isNullOrEmpty()){
            return 0L
        }
        return try {
            val date = dateFormat.parse(utcString)
            date?.toInstant()?.epochSecond ?: 0L
        } catch (e: Exception) {
            // Handle parsing error
            0L
        }
    }

    fun validateUTCString(utcString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        try {
            val date = dateFormat.parse(utcString)
            return date != null
        } catch (e: Exception) {
            // Handle parsing error
            return false
        }
    }

    fun getCurrentTimestamp() = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time

    fun getCurrentTimestampEpoch() = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time.toInstant().epochSecond
}