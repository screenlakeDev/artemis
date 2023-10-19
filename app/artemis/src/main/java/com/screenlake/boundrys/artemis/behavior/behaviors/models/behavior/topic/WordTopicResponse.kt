package com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic

class WordTopicResponse(val id: Int, val word: String, val isSynonym: Boolean, val localGroupQueryId: String, val queryId: String, val timestamp: Long?, val wordsAroundSeen: List<String>)