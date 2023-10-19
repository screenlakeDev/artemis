package com.screenlake.boundrys.artemis.repository

import android.content.Context
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.scroll.ScrollEventSegment
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.SearchResult
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.TopicSeenInterval
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.WordTopic
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.WordTopicResponse
import com.screenlake.boundrys.artemis.database.AccessibilityEvent
import com.screenlake.boundrys.artemis.database.*
import com.screenlake.boundrys.artemis.search.TopicSearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GenOp(val context: Context) {
    suspend fun searchTopic(wordTopic: WordTopic): List<WordTopicResponse> {
        val responses = mutableListOf<WordTopicResponse>()

        // Search for the main word
        val mainWordResult = searchASEvent(wordTopic.word)
        if (mainWordResult.isNotEmpty()) {
            mainWordResult.forEach {
                val response = WordTopicResponse(
                    id = responses.size + 1,
                    word = wordTopic.word,
                    isSynonym = false,
                    localGroupQueryId = "group_${responses.size + 1}",
                    queryId = "",
                    timestamp = it.eventTime,
                    wordsAroundSeen = TopicSearch.extractWordsAround(SearchResult(it.text ?: ""), wordTopic)
                )
                responses.add(response)
            }
        }

        // Search for synonyms
        for (synonym in wordTopic.synonyms) {
            val synonymResult = searchASEvent(synonym)
            if (synonymResult.isNotEmpty()) {
                synonymResult.forEach {
                    val response = WordTopicResponse(
                        id = responses.size + 1,
                        word = synonym,
                        isSynonym = true,
                        localGroupQueryId = "group_${responses.size + 1}",
                        queryId = "",
                        timestamp = it.eventTime,
                        wordsAroundSeen = TopicSearch.extractWordsAround(SearchResult(it.text ?: ""), wordTopic)
                    )
                    responses.add(response)
                }
            }
        }

        return responses
    }

    fun getString(resId: Int) = context.getString(resId)

    suspend fun deleteUser() =
        DatabaseModule.provideAppDatabase(context).getUserDao.deleteUser()

    suspend fun deleteAllScreenshotZip() =
        DatabaseModule.provideAppDatabase(context).getScreenshotZipDao.nukeTable()

    fun save(accessibilityEvent: AccessibilityEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseModule.provideAppDatabase(context).getAccessibilityEventDAO.save(
                accessibilityEvent
            )
        }
    }

    suspend fun save(accessibilityEvents: List<AccessibilityEvent>) {
        DatabaseModule.provideAppDatabase(context).getAccessibilityEventDAO.save(accessibilityEvents)
    }

    suspend fun deleteZip(id: Int) =
        context.let { DatabaseModule.provideAppDatabase(it).getScreenshotZipDao.delete(id) }

    fun deleteZipSynchronous(id: Int) =
        context.let { DatabaseModule.provideAppDatabase(it).getScreenshotZipDao.deleteSynchronous(id) }

    suspend fun getASEvents() =
        context.let {
            DatabaseModule.provideAppDatabase(it).getAccessibilityEventDAO.getAllAccessibilityEvents(
                500
            )
        }

    suspend fun deleteZipFlag(id: Int) =
        context.let {
            DatabaseModule.provideAppDatabase(it).getScreenshotZipDao.flagZipForDeletion(
                id,
                true
            )
        }

    suspend fun getUser(): User {
        return DatabaseModule.provideAppDatabase(context).getUserDao.getUser()
    }

    suspend fun deleteAccessibilityEvents(ids: List<Int>) =
        context.let {
            DatabaseModule.provideAppDatabase(it).getAccessibilityEventDAO.deleteAccessibilityEvents(
                ids
            )
        }

    fun deleteLogEvents(logEvents: List<Int>) =
        context.let { DatabaseModule.provideAppDatabase(it).getLogEventDAO.deleteLogEvents(logEvents) }


    fun insertScreenshotZip(screenshotZip: ScreenshotZip) =
        context.let {
            DatabaseModule.provideAppDatabase(it).getScreenshotZipDao.insertZipObj(
                screenshotZip
            )
        }

    suspend fun deleteScreenshotZip(screenshotZip: ScreenshotZip) =
        context.let {
            DatabaseModule.provideAppDatabase(it).getScreenshotZipDao.deleteZipObj(
                screenshotZip
            )
        }

    fun deleteScreenshotZipSync(screenshotZip: ScreenshotZip) =
        context.let {
            DatabaseModule.provideAppDatabase(it).getScreenshotZipDao.deleteZipObjSync(
                screenshotZip
            )
        }

    suspend fun getZipCount() =
        context.let { DatabaseModule.provideAppDatabase(it).getScreenshotZipDao.getZipCount() }

    suspend fun getZipsToUpload() =
        context.let { DatabaseModule.provideAppDatabase(it).getScreenshotZipDao.getAllZipObjs() }

    fun getZipsToUploadSynchronously() =
        context.let { DatabaseModule.provideAppDatabase(it).getScreenshotZipDao.getAllZipObjsSynchronously() }


    suspend fun getLogs(limit: Int, offset: Int) =
        context.let {
            DatabaseModule.provideAppDatabase(it).getLogEventDAO.getLogsFrom(
                limit,
                offset
            )
        }

    suspend fun logCount() =
        context.let { DatabaseModule.provideAppDatabase(it).getLogEventDAO.logCount() }

    suspend fun getZipsToDelete() =
        context.let { DatabaseModule.provideAppDatabase(it).getScreenshotZipDao.getZipToDeleteFlagged() }

    suspend fun searchASEvent(search: String) =
        DatabaseModule.provideAppDatabase(context).getAccessibilityEventDAO.search(search)

    fun saveTopicsSeen(topicSeenInterval: List<TopicSeenInterval>){
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseModule.provideAppDatabase(context).getTopicSeenDAO.saveAll(
                topicSeenInterval
            )
        }
    }

    fun saveScrollEvents(scrollEventSegments: List<ScrollEventSegment>) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseModule.provideAppDatabase(context).getScrollEventlDAO.saveAll(
                scrollEventSegments
            )
        }
    }
}
