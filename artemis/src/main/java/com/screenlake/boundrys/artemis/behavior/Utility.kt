package com.screenlake.boundrys.artemis.behavior

import com.google.gson.Gson
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.SearchResult
import com.screenlake.boundrys.artemis.database.AccessibilityEvent

class NonUniqueIdsException(duplicateIds: Set<Int>) : Exception("Duplicate ids found: $duplicateIds")

fun AccessibilityEvent.toSearchResult() : SearchResult {
    return SearchResult(this.text ?: "")
}

object Utility {
    fun ocrCleanUp(input: String) : String {
        val ocrRegex1 = """[\'\",\r\n;\t\\]""".toRegex()
        val output1 = input.replace(ocrRegex1, " ")

        val ocrRegex2 = "[\\[\\]]".toRegex()
        val output2 = output1.replace(ocrRegex2, " ")

        val ocrRegex3 = """\s{2,}""".toRegex()
        return output2.replace(ocrRegex3, " ")
    }

    fun getEpochTime(time: String): Long {
        return when (time) {
            "15_MINUTES" -> 15 * 60 // 15 minutes in seconds
            "30_MINUTES" -> 30 * 60 // 30 minutes in seconds
            "45_MINUTES" -> 45 * 60 // 45 minutes in seconds
            "1_HOUR" -> 60 * 60 // 60 minutes in seconds
            "2_HOUR" -> 2 * 60 * 60 // 60 minutes in seconds
            "3_HOUR" -> 3 * 60 * 60 // 60 minutes in seconds
            "4_HOUR" -> 5 * 60 * 60 // 60 minutes in seconds
            else -> 0L
        }
    }

    fun now() = System.currentTimeMillis()/1000L

    fun convertToAccessibilityEvent(accessibilityEventString: String): AccessibilityEvent? {
        val jsonString = accessibilityEventString.replace(';', ',')
        return try {
            Gson().fromJson(jsonString, AccessibilityEvent::class.java)
        } catch (e: Exception) {
            null
        }
    }
}