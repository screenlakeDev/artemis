package com.screenlake.boundrys.artemis.behavior

import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.TopicSeenInterval
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.WordTopic
import com.screenlake.boundrys.artemis.utility.TimeUtility

// TextSeenEventHandler class
class TextSeen(foundWords: MutableList<TopicSeenInterval>? = null, vadData: Map<String, DoubleArray>? = null) {
    var wordTopics: Map<String, WordTopic> = hashMapOf()
    var foundWords = foundWords ?: mutableListOf()
    var vadData: Map<String, DoubleArray> = vadData ?: hashMapOf()
    val wordHash = HashSet<String>()

    fun findWordsInText(text: String, apk: String) {
        val noPunctuation = removePunctuation(text)
        val wordsInText = noPunctuation.split(" ") // Split the text into individual words
        for (i in wordsInText.indices) {
            val currentWord = wordsInText[i] // Get the current word in the loop
            // Check if the current word matches the word or any synonyms in the WordTopic
            if (wordTopics.containsKey(currentWord)) {
                val wordTopic = wordTopics[currentWord] ?: continue

                val wordsAround = getWordsAroundIndex(wordsInText, i)
                val wordsAroundString = wordsAround.joinToString(separator = ", ")
                if (wordHash.contains(wordsAroundString)) {
                    continue
                }

                wordHash.add(wordsAroundString)
                val sentimentScore = getSentiment(wordsAround)

                val sentiment = scoreToSentiment(sentimentScore)

                val topicSeen = TopicSeenInterval(wordTopic.word, wordTopic.synonyms.contains(wordTopic.word), apk, TimeUtility.getCurrentTimestampEpoch(), "", "",
                    "", "", sentiment, sentimentScore)

                topicSeen.wordsAround = wordsAroundString
                foundWords.add(topicSeen) // Add the found phrase to the list of found words
            }
        }
    }

    fun getWordsAroundIndex(words: List<String>, index: Int): List<String> {
        val startIndex = maxOf(0, index - 5)
        val endIndex = minOf(words.size - 1, index + 5)
        return words.subList(startIndex, endIndex + 1)
    }

    fun analyzeText(text: String, apk: String) {
        findWordsInText(text, apk)
    }

    fun removePunctuation(input: String): String {
        return input.replace(Regex("[^a-zA-Z0-9\\s]"), "")
    }

    private fun getSentiment(words: List<String>) : Double {
        var totalSentimentScore = 0.0
        var denominator = 0
        for (word in words) {
            val vadValues = vadData.getVADValues(word)
            val valence = vadValues[0]

            if (valence > 0){
                denominator++
                totalSentimentScore += valence
            }
        }

        val result = if (denominator == 0) 0.0 else totalSentimentScore.div(words.size)

        val sentiment = scoreToSentiment(result)
        println("Sentence: -> ${words.joinToString(separator = " ")} <-, Valence: $result, Sentiment: $sentiment")
        return result
    }

    private fun scoreToSentiment(totalSentimentScore: Double): String {
        val sentiment =
            if (totalSentimentScore > 0.0) "positive" else if (totalSentimentScore < 0.0) "negative" else "neutral"
        return sentiment
    }

    fun Map<String, DoubleArray>.getVADValues(word: String?): DoubleArray {
        return vadData.getOrDefault(word, doubleArrayOf(0.0, 0.0, 0.0))
    }
}