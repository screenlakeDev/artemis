package com.screenlake.boundrys.artemis.behavior.behaviors.handlers

import com.screenlake.boundrys.artemis.behavior.Utility.ocrCleanUp
import com.screenlake.boundrys.artemis.behavior.behaviors.models.common.BehavioralEvents
import com.screenlake.boundrys.artemis.database.AccessibilityEvent
import com.screenlake.boundrys.artemis.repository.GenOp
import timber.log.Timber

// TextSeenEventHandler class
class SelectedEventHandler(genOp: GenOp?) : EventHandler {
    var generalOperations: GenOp? = null;
    init {
        generalOperations = genOp
    }

    override fun canHandleEvent(event: AccessibilityEvent): Boolean {
        // Check if the event involves text seen on the screen
        // Return true if the event can be handled by this handler
        // Return false otherwise
        return true
    }

    override fun handleEvent(event: AccessibilityEvent) {
        // Handle the text seen event
        // Perform specific actions or processing for text seen on the screen
        when(event.behavorType) {
            BehavioralEvents.CLICKED_EVENT -> selectedEvent(event)
//            BehavioralEvents.SESSION_OVER -> sessionOverEvent()
            else -> {

            }
        }
    }

    private fun selectedEvent(accessibilityEventConverted: AccessibilityEvent) {
        if (accessibilityEventConverted?.text?.trim()?.isNotEmpty() == true
            && accessibilityEventConverted.text != "[]"
        ) {
            accessibilityEventConverted.text =
                ocrCleanUp(accessibilityEventConverted.text ?: "")
            accessibilityEventConverted.eventType = accessibilityEventConverted.behavorType?.name
            Timber.tag("Check")
                .d(accessibilityEventConverted.text + " : " + accessibilityEventConverted.contentDescription)
            save(accessibilityEventConverted, generalOperations)
        }
    }

    fun save(accessibilityEvent: AccessibilityEvent, generalOperations: GenOp? = null) {
        generalOperations?.save(accessibilityEvent)
    }
}