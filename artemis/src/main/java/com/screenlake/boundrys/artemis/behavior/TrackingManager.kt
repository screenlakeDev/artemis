package com.screenlake.boundrys.artemis.behavior

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.screenlake.boundrys.artemis.repository.GenOp
import com.screenlake.boundrys.artemis.behavior.behaviors.handlers.EventHandler
import com.screenlake.boundrys.artemis.behavior.behaviors.handlers.ScrollEventHandler
import com.screenlake.boundrys.artemis.behavior.behaviors.handlers.AllTextHandler
import com.screenlake.boundrys.artemis.behavior.behaviors.handlers.SelectedEventHandler
import com.screenlake.boundrys.artemis.database.AccessibilityEvent


// TrackingManager class
class TrackingManager(private val context: Context) {
    // List of event handlers
    var eventHandlers: MutableList<EventHandler>

    // Handle the accessibility event
    fun handleAccessibilityEvent(event: AccessibilityEvent) {
        // Iterate through each event handler and pass the event
        for (handler in eventHandlers) {
            if (handler.canHandleEvent(event)) {
                handler.handleEvent(event)
            }
        }
    }

    // Handle the accessibility event
    fun handleAccessibilityEvent(event: AccessibilityNodeInfo) {
        // Iterate through each event handler and pass the event
        for (handler in eventHandlers) {
            if (handler.canHandleEvent(event)) {
                handler.handleEvent(event)
            }
        }
    }

    // Constructor
    init {
        // Initialize eventHandlers with appropriate event handler instances
        val generalOperations = GenOp(context)
        eventHandlers = ArrayList()
        eventHandlers.add(ScrollEventHandler(generalOperations))
        eventHandlers.add(SelectedEventHandler(generalOperations))
        eventHandlers.add(AllTextHandler(generalOperations, context))
        // Add more event handlers as needed
    }
}



// Additional event handlers and event-specific logic can be added similarly
