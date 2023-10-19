package com.screenlake.boundrys.artemis.search

import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.SearchResult
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.WordTopic
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.WordTopicResponse
import com.screenlake.boundrys.artemis.repository.GenOp
import java.util.UUID

class SearchSvc(genOp: GenOp) {
    private lateinit var generalOperations: GenOp
    init {
        generalOperations = genOp
    }
    suspend fun searchTopic(wordTopic: WordTopic): List<WordTopicResponse> {
        val groupId = UUID.randomUUID().toString()
        val responses = mutableListOf<WordTopicResponse>()

        // Search for the main word
        val mainWordResult = generalOperations.searchASEvent(wordTopic.word)
        if (mainWordResult.isNotEmpty()) {
            mainWordResult.forEach {
                val response = WordTopicResponse(
                    id = responses.size + 1,
                    word = wordTopic.word,
                    isSynonym = false,
                    queryId = "",
                    localGroupQueryId = groupId,
                    timestamp = it.eventTime,
                    wordsAroundSeen = TopicSearch.extractWordsAround(SearchResult(it.text ?: ""), wordTopic)
                )
                responses.add(response)
            }
        }

        // Search for synonyms
        for (synonym in wordTopic.synonyms) {
            val synonymResult = generalOperations.searchASEvent(synonym)
            if (synonymResult.isNotEmpty()) {
                synonymResult.forEach {
                    val response = WordTopicResponse(
                        id = responses.size + 1,
                        word = synonym,
                        isSynonym = true,
                        queryId = "",
                        localGroupQueryId = groupId,
                        timestamp = it.eventTime,
                        wordsAroundSeen = TopicSearch.extractWordsAround(SearchResult(it.text ?: ""), wordTopic)
                    )
                    responses.add(response)
                }
            }
        }

        return responses
    }
}