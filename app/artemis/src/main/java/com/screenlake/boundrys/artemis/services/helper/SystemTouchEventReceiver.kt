package com.screenlake.boundrys.artemis.services.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.screenlake.boundrys.artemis.database.AccessibilityEvent
import com.screenlake.boundrys.artemis.repository.DatabaseModule
import com.screenlake.boundrys.artemis.services.TouchAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*

class SystemTouchEventReceiver : BroadcastReceiver() {
    private var screenOff = false
    private var sessionStart = false

    override fun onReceive(context: Context, intent: Intent) {

        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> {
                TouchAccessibilityService.isScreenOn.set(false)

                // We can only have a session end with a session start.
                if (sessionStart){
                    saveSessionEnd(context)
                }
            }
            Intent.ACTION_USER_PRESENT -> {
                saveSessionStart(context)
            }
        }
    }

    private fun saveSessionStart(context: Context) {
        TouchAccessibilityService.sessionStartTime = Instant.now().epochSecond
        TouchAccessibilityService.appAccessibilitySessionId = UUID.randomUUID().toString()

        sessionStart = true
        screenOff = false
        TouchAccessibilityService.isScreenOn.set(true)

        CoroutineScope(Dispatchers.IO).launch {
            DatabaseModule.provideAppDatabase(context).getAccessibilityEventDAO.save(
                AccessibilityEvent(
                    user = TouchAccessibilityService.user!!.username,
                    eventTime = TouchAccessibilityService.sessionStartTime,
                    eventType = "SESSION_START",
                    appIntervalId = TouchAccessibilityService.appIntervalId,
                    accessibilitySessionId = TouchAccessibilityService.appAccessibilitySessionId
                )
            )
        }
    }

    private fun saveSessionEnd(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseModule.provideAppDatabase(context).getAccessibilityEventDAO.save(
                AccessibilityEvent(
                    user = TouchAccessibilityService.user!!.username,
                    eventTime = Instant.now().epochSecond,
                    eventType = "SESSION_END",
                    appIntervalId = TouchAccessibilityService.appIntervalId,
                    accessibilitySessionId = TouchAccessibilityService.appAccessibilitySessionId
                )
            )

            sessionStart = false
        }
    }
}