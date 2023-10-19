package com.screenlake.boundrys.artemis.behavior.behaviors.handlers

import android.content.Context
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import android.webkit.URLUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.screenlake.boundrys.artemis.behavior.NonUniqueIdsException
import com.screenlake.boundrys.artemis.behavior.TextSeen
import com.screenlake.boundrys.artemis.behavior.Utility
import com.screenlake.boundrys.artemis.behavior.behaviors.models.common.BehavioralEvents
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.TopicSeenInterval
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.WordTopic
import com.screenlake.boundrys.artemis.repository.GenOp
import com.screenlake.boundrys.artemis.database.AccessibilityEvent
import com.screenlake.boundrys.artemis.services.TouchAccessibilityService
import com.screenlake.boundrys.artemis.utility.TimeUtility
import com.screenlake.boundrys.artemis.behavior.behaviors.handlers.EventHandler
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class AllTextHandler(genOp: GenOp?, private var context: Context) : EventHandler {
    var previousKey = "";
    var generalOperations: GenOp? = null;
    var textSeen: TextSeen? = null
    init {
        generalOperations = genOp
        textSeen = TextSeen()
        textSeen!!.vadData = loadDictionary()
        textSeen!!.wordTopics = initWordTopics()
    }

    override fun canHandleEvent(event: AccessibilityEvent): Boolean {
        return when(event.behavorType) {
            BehavioralEvents.SESSION_OVER -> true
            else -> false
        }
    }

    override fun handleEvent(event: AccessibilityEvent) {
        when(event.behavorType) {
            BehavioralEvents.SESSION_OVER -> {
                sessionOver()
            }
            else -> {

            }
        }
    }

    override fun canHandleEvent(event: AccessibilityNodeInfo): Boolean {
        return true
    }

    override fun handleEvent(event: AccessibilityNodeInfo) {
        extractTextFromRoot(event)
    }

    private fun extractTextFromRoot(rootNode: AccessibilityNodeInfo?) {
        //Timber.tag("AccessibilityEvent").d("Getting screen components.")
//        if (rootNode != null && rootNode.packageName.contains("com.android.chrome")) {
        if (rootNode != null) {
            val packageName = rootNode.packageName.toString()
            val result = mutableListOf<AccessibilityEvent>()
            var groupdId = UUID.randomUUID().toString()
            val currentTime = TimeUtility.getCurrentTimestampEpoch()

            val rect = Rect()
            rootNode.getBoundsInScreen(rect)

            val maxBot = Math.abs(rect.bottom)
            val maxRight = Math.abs(rect.right)

            var resultText = mutableListOf<String>()
            traverseNode(rootNode, result, resultText, maxBot, maxRight)
            rootNode.recycle()

            handleScreenText(result, resultText, packageName, currentTime)
        }
    }

    private fun handleScreenText(
        result: MutableList<AccessibilityEvent>,
        resultText: MutableList<String>,
        packageName: String,
        currentTime: Long
    ) {
//        if (result.isNotEmpty()) {
//            CoroutineScope(Dispatchers.IO).launch {
//                save(result, generalOperations)
//            }
//        }

        val joinedText = resultText.joinToString()
        val text = Utility.ocrCleanUp(joinedText)

        if (resultText.isNotEmpty() && resultText.joinToString() != previousKey) {
            println("****$resultText")
            textSeen?.analyzeText(text, packageName)
            previousKey = joinedText
            save(
                AccessibilityEvent(
                    user = TouchAccessibilityService.user!!.username,
                    packageName = packageName,
                    appIntervalId = TouchAccessibilityService.appIntervalId,
                    text = Utility.ocrCleanUp(joinedText),
                    eventTime = currentTime,
                    eventType = "SCREEN_TEXT",
                    accessibilitySessionId = TouchAccessibilityService.appAccessibilitySessionId
                ), generalOperations
            )
        }
    }

    private fun traverseNode(node: AccessibilityNodeInfo, result:MutableList<AccessibilityEvent>, resultText:MutableList<String>, maxBot:Int, maxRight:Int) {
        if (node.childCount == 0) {
            // Leaf node, retrieve text
            val text = node.text
            val currentTime = TimeUtility.getCurrentTimestampEpoch()
            if (!text.isNullOrEmpty()) {
                // Do something with the text
                //println(">>> ${node.toString()} <<<")
                if(text.contains(".com") && node.className.contains("android.widget.EditText")){
                    println("***** ${URLUtil.isValidUrl("http://" + text.toString())} **** $text ****")
                    val currentUrl = text.toString()
                    if (currentUrl != TouchAccessibilityService.prevUrl && URLUtil.isValidUrl("http://" + currentUrl)){
                        result.add(
                            AccessibilityEvent(
                                user = TouchAccessibilityService.user?.username,
                                eventType = "URL",
                                text = currentUrl,
                                eventTime = currentTime,
                                packageName = node.packageName.toString(),
                                accessibilitySessionId = TouchAccessibilityService.appAccessibilitySessionId,
                                appIntervalId = TouchAccessibilityService.appIntervalId
                            )
                        )
                        TouchAccessibilityService.prevUrl = currentUrl
                    }

                }
                val rect = android.graphics.Rect()
                node.getBoundsInScreen(rect)

                if ((rect.left in 0..maxRight)
                    && (rect.top in 0..maxBot)
                    && (rect.right in 0..maxBot)
                    && (rect.bottom in 0..maxBot)){
                    val combined = text.toString() + "\n"
                    if (node.className.contains("android.widget.Image")){
                        if (TouchAccessibilityService.prevMeta != text.toString()){
                            result.add(AccessibilityEvent().apply {
                                this.user = TouchAccessibilityService.user?.username
                                this.eventType = "IMAGE_METADATA"
                                this.text = text.toString()
                                this.eventTime = currentTime
                                this.packageName = node.packageName.toString()
                                this.accessibilitySessionId =
                                    TouchAccessibilityService.appAccessibilitySessionId
                                this.appIntervalId = TouchAccessibilityService.appIntervalId
                            })
                            TouchAccessibilityService.prevMeta = text.toString()
                        }
                    }else{
                        resultText.add(combined)
                    }

                }
            }
        } else {
            // Parent node, traverse children
            for (i in 0 until node.childCount) {
                val childNode = node.getChild(i)
                childNode ?: return

                traverseNode(childNode, result, resultText, maxBot, maxRight)
                childNode.recycle()
            }
        }
    }

    @Throws(IOException::class)
    private fun loadDictionary() : MutableMap<String, DoubleArray> {
        val data = mutableMapOf<String, DoubleArray>()
        val assetManager = context?.assets
        assetManager?.open("NRC-VAD-Lexicon.txt").use { inputStream ->
            val scanner = Scanner(inputStream)
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()
                val parts =
                    line.split("\t".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                if (parts.size >= 4) {
                    val word = parts[0]
                    val valence = parts[1].toDouble()
                    val arousal = parts[2].toDouble()
                    val dominance = parts[3].toDouble()
                    val vadValues =
                        doubleArrayOf(valence, arousal, dominance)
                    data[word] = vadValues
                }
            }

            return data
        }
    }

    private fun readWordTopicsFromJsonFile(): List<WordTopic> {

        val inputStream = context?.applicationContext?.assets?.open("topics.json")

        val jsonReader = BufferedReader(InputStreamReader(inputStream))
        val jsonType = object : TypeToken<List<WordTopic>>() {}.type
        val wordTopics: List<WordTopic> = Gson().fromJson(jsonReader, jsonType)
        jsonReader.close()
        val filteredWordTopics = wordTopics
            .filterNotNull() // Filter out null items
            .mapNotNull { wordTopic ->
                // Filter out WordTopic instances with null attributes
                if (wordTopic.word != null && wordTopic.synonyms != null) {
                    wordTopic
                } else {
                    null
                }
            }
        return filteredWordTopics
    }

    private fun initWordTopics(): Map<String, WordTopic> {
        val rawTopics = readWordTopicsFromJsonFile()
        checkUniqueIds(rawTopics)

        return createWordTopicDictionary(rawTopics)
    }

    private fun createWordTopicDictionary(wordTopics: List<WordTopic>): Map<String, WordTopic> {
        val dictionary = mutableMapOf<String, WordTopic>()

        for (wordTopic in wordTopics) {
            if (!dictionary.containsKey(wordTopic.word)) {
                dictionary[wordTopic.word] = wordTopic
            }

            for (synonym in wordTopic.synonyms) {
                if (!dictionary.containsKey(synonym)) {
                    dictionary[synonym] = wordTopic
                }
            }
        }

        return dictionary
    }

    private fun checkUniqueIds(topics: List<WordTopic>) {
        val idCounts = topics.groupBy { it.id }.mapValues { it.value.size }
        val duplicateIds = idCounts.filterValues { it > 1 }.keys

        if (duplicateIds.isNotEmpty()) {
            throw NonUniqueIdsException(duplicateIds)
        }
    }

    private fun sessionOver() {
        textSeen?.wordHash?.clear()
        save()
    }

    fun save(foundWords: List<TopicSeenInterval>? = null, generalOperations: GenOp? = null) {
        if (generalOperations != null){
            this.textSeen?.foundWords?.let { generalOperations.saveTopicsSeen(it) }
        }else{
            this.textSeen?.foundWords?.let { this.generalOperations?.saveTopicsSeen(it) }
        }

    }

    fun save(accessibilityEvent: AccessibilityEvent, generalOperations: GenOp? = null) {
        generalOperations?.save(accessibilityEvent)
    }

    suspend fun save(accessibilityEvents: List<AccessibilityEvent>, generalOperations: GenOp? = null) {
        generalOperations?.save(accessibilityEvents)
    }
}