package com.screenlake.boundrys.artemis.search

import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.SearchResult
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.WordTopic

object TopicSearch {
    fun extractWordsAround(searchResult: SearchResult, wordTopic: WordTopic): List<String> {
        val result = mutableListOf<String>()

        val targetWord = wordTopic.word
        val wordsAroundWindow = wordTopic.wordsAroundWindow
        val wordsAround = wordTopic.wordsAround

        val words = searchResult.text.split("\\s+".toRegex())

        for (i in words.indices) {
            if (words[i] == targetWord) {
                val startIndex = maxOf(i - wordsAroundWindow, 0)
                val endIndex = minOf(i + wordsAroundWindow + 1, words.size)

                val wordsExtracted = words.slice(startIndex until endIndex)
                val wordsToAppend = hashSetOf<String>()

                for (j in wordsExtracted.indices) {
                    if (j != wordsAroundWindow) {
                        wordsToAppend.add(wordsExtracted[j])
                    }
                }

                wordsExtracted.forEach { extractedWord ->
                    if (wordsAround.contains(extractedWord) && extractedWord != targetWord) {
                        result.add(extractedWord)
                    }
                }
            }
        }

        return result
    }
}