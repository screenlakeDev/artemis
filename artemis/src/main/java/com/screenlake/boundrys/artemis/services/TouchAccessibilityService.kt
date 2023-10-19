package com.screenlake.boundrys.artemis.services

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.screenlake.boundrys.artemis.behavior.TrackingManager
import com.screenlake.boundrys.artemis.behavior.behaviors.models.common.BehavioralEvents
import com.screenlake.boundrys.artemis.database.AccessibilityEvent
import com.screenlake.boundrys.artemis.repository.DatabaseModule
import com.screenlake.boundrys.artemis.services.helper.SystemTouchEventReceiver
import com.screenlake.boundrys.artemis.utility.TimeUtility
import com.screenlake.boundrys.artemis.database.User
import kotlinx.coroutines.*
import timber.log.Timber
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class TouchAccessibilityService : AccessibilityService() {

    companion object {
        val isScreenOn = AtomicBoolean()
        var prevUrl = ""
        var prevMeta = ""
        var appAccessibilitySessionId = UUID.randomUUID().toString()
        var framesPerSecond = 3333L
        var sessionStartTime: Long? = null
        var appIntervalId = UUID.randomUUID().toString()
        var user: User? = null
    }

    var mReceiver: BroadcastReceiver? = null
    lateinit var trackingManager: TrackingManager

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onAccessibilityEvent(accessibilityEvent: android.view.accessibility.AccessibilityEvent) {

//        Timber.tag("Check").d("{" + accessibilityEvent.toString().replace(';', ',') + "}")
        var accessibilityEventString = convert(accessibilityEvent)

        when(accessibilityEvent.eventType) {
            android.view.accessibility.AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                typeViewClickedEvent(accessibilityEventString)
            }
            android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SELECTED -> {
                typeViewSelectedEvent(accessibilityEventString)
            }

            android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                typeViewScrolledEvent(accessibilityEventString)
            }
        }
    }

    private fun convert(accessibilityEvent: android.view.accessibility.AccessibilityEvent): String {
        var accessibilityEventString = accessibilityEvent.toString().replace(';', ',')

        // Split the JSON string into key-value pairs and build a proper JSON string
        // Split the JSON string into key-value pairs and build a proper JSON string
        val keyValuePairs: Array<String> = accessibilityEventString.split(",").toTypedArray()
        val properJsonString = StringBuilder("{")
        for (keyValuePair in keyValuePairs) {
            val parts = keyValuePair.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (parts.size == 2) {
                val key = parts[0].trim { it <= ' ' }
                val value = parts[1].trim { it <= ' ' }
                properJsonString.append("\"").append(key).append("\":").append("\"").append(value)
                    .append("\",")
            }
        }
        properJsonString.deleteCharAt(properJsonString.length - 1) // Remove the last comma

        accessibilityEventString = properJsonString.append("}").toString()
        return accessibilityEventString
    }

    private fun typeViewClickedEvent(accessibilityEventString: String) {
        println(accessibilityEventString)
        val accessibilityEventConverted = convertToAccessibilityEvent(accessibilityEventString, BehavioralEvents.CLICKED_EVENT)

        if (accessibilityEventConverted != null) {
            trackingManager.handleAccessibilityEvent(accessibilityEventConverted)
        }
    }

    private fun typeViewScrolledEvent(accessibilityEventString: String) {
        val accessibilityEventConverted = convertToAccessibilityEvent(accessibilityEventString, BehavioralEvents.SWIPE_EVENT)
        if (accessibilityEventConverted != null) {
            trackingManager.handleAccessibilityEvent(accessibilityEventConverted)
        }
    }

    private fun typeViewSelectedEvent(accessibilityEventString: String) {
        val accessibilityEventConverted = convertToAccessibilityEvent(accessibilityEventString, BehavioralEvents.SELECTED_EVENT)

        if (isScreenOn.get()
            && accessibilityEventConverted?.text?.isNotEmpty() == true && accessibilityEventConverted.text != "[]"
        ) {
            trackingManager.handleAccessibilityEvent(accessibilityEventConverted)

        }
    }

    fun convertToAccessibilityEvent(accessibilityEventString: String, event: BehavioralEvents): com.screenlake.boundrys.artemis.database.AccessibilityEvent? {
        val jsonString = accessibilityEventString.replace(';', ',')
        val epoch = TimeUtility.getCurrentTimestampEpoch()
        return try {
            val accessibilityEventConverted = Gson().fromJson(jsonString, com.screenlake.boundrys.artemis.database.AccessibilityEvent::class.java)
            accessibilityEventConverted?.behavorType = event
            accessibilityEventConverted?.eventTime = epoch
            accessibilityEventConverted?.user = user?.username
            accessibilityEventConverted?.accessibilitySessionId = appAccessibilitySessionId
            accessibilityEventConverted?.appIntervalId = appIntervalId
            accessibilityEventConverted
        } catch (e: Exception) {
            null
        }
    }

    override fun onGesture(gestureId: Int): Boolean {
        Log.d("TAG","onGesture:"+gestureId);
        return super.onGesture(gestureId)
    }

    override fun onInterrupt(){
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onServiceConnected() {
        trackingManager = TrackingManager(this)
        Timber.tag("Check").d("Happy")
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)

        mReceiver = SystemTouchEventReceiver()
        registerReceiver(mReceiver, filter)

        isScreenOn.set(true)

        CoroutineScope(Dispatchers.IO).launch {
            supervisorScope {
                startAccessibilityService()
            }
        }

        super.onServiceConnected()
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun startAccessibilityService() {
        user = getUser()
        save(
            AccessibilityEvent(
            user = user?.username,
            eventTime = Instant.now().epochSecond,
            eventType = "SESSION_START",
            appIntervalId = appIntervalId,
            accessibilitySessionId = appAccessibilitySessionId
        )
        )

        sessionStartTime = Instant.now().epochSecond
        var firstRun = true
        var off = true
        while (true){
            yield()
            delay(framesPerSecond)

            if (!firstRun){
                appIntervalId = UUID.randomUUID().toString()
            }else{
                firstRun = false
            }

            if(!isScreenOn.get()){
                if (off) {
                    val screenOffEvent = AccessibilityEvent()
                    screenOffEvent.behavorType = BehavioralEvents.SESSION_OVER
                    trackingManager.handleAccessibilityEvent(screenOffEvent)
                    off = false
                }

                continue
            }else{
                off = true
            }

            val rootNode = rootInActiveWindow
            if(rootNode != null) {
                trackingManager.handleAccessibilityEvent(rootNode)
            }
        }
    }

    private fun AccessibilityNodeInfo.toAccessibilityEvent(currentTime:Long, groupId:String): AccessibilityEvent {

        val rect = android.graphics.Rect()
        this.getBoundsInScreen(rect)
        val accessibilityInfo = this

        return AccessibilityEvent().apply {
            this.eventTime = currentTime
            this.eventGroupId = groupId
            this.className = accessibilityInfo.className.toString()
            this.text = accessibilityInfo.text.toString()
            this.contentDescription = accessibilityInfo?.contentDescription?.toString()
            this.password = accessibilityInfo?.isPassword
            this.r1 = rect.left
            this.r2 = rect.top
            this.r3 = rect.right
            this.r4 = rect.bottom
        }
    }

    private fun List<AccessibilityEvent>.addCoordinates(): Int {
        return this.sumOf { it.r1!! + it.r2!! + it.r3!! + it.r4!! }
    }

    private fun android.view.accessibility.AccessibilityEvent.toAccessibilityEvent(currentTime:Long, groupId:String, event:String): AccessibilityEvent {
        val accessibilityEvent = this
        return AccessibilityEvent().apply {
            this.eventType = event
            this.eventTime = currentTime
            this.eventGroupId = groupId
            this.className = accessibilityEvent.className?.toString()
            this.text = accessibilityEvent.getText().joinToString(separator = ", ")
            this.contentDescription = accessibilityEvent?.contentDescription?.toString()
            this.password = accessibilityEvent?.isPassword
        }
    }

    private suspend fun save(accessibilityEvent: AccessibilityEvent) {
        DatabaseModule.provideAppDatabase(this@TouchAccessibilityService).getAccessibilityEventDAO.save(accessibilityEvent)
    }

    private suspend fun getUser(): User {
        return DatabaseModule.provideAppDatabase(this@TouchAccessibilityService).getUserDao.getUser()
    }
}