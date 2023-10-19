package com.screenlake.boundrys.artemis.behavior.behaviors.handlers

import android.view.accessibility.AccessibilityNodeInfo
import com.screenlake.boundrys.artemis.database.AccessibilityEvent

interface EventHandler {
    fun canHandleEvent(event: AccessibilityEvent): Boolean {
        return true
    }

    fun handleEvent(event: AccessibilityEvent){

    }

    fun handleEvent(event: AccessibilityNodeInfo){

    }

    fun canHandleEvent(event: AccessibilityNodeInfo): Boolean{
        return false
    }
}