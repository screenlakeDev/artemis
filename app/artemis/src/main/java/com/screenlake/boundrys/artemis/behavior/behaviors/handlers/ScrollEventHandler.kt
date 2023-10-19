package com.screenlake.boundrys.artemis.behavior.behaviors.handlers

import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.scroll.ScrollEventInterval
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.scroll.ScrollEventSegment
import com.screenlake.boundrys.artemis.behavior.behaviors.models.common.BehavioralEvents
import com.screenlake.boundrys.artemis.database.AccessibilityEvent
import com.screenlake.boundrys.artemis.repository.GenOp
import com.screenlake.boundrys.artemis.services.TouchAccessibilityService
import java.util.*
import kotlin.math.abs

// SwipeEventHandler class
class ScrollEventHandler(genOp: GenOp?) : EventHandler {
    var swIntervals = LinkedList<MutableList<ScrollEventInterval>>()
    var swSegments = mutableListOf<ScrollEventSegment>()

    var generalOperations: GenOp? = null;
    init {
        generalOperations = genOp
    }

    override fun canHandleEvent(event: AccessibilityEvent): Boolean {
        // Check if the event is a swipe gesture
        // Return true if the event can be handled by this handler
        // Return false otherwise

        return when(event.behavorType) {
            BehavioralEvents.SWIPE_EVENT -> true
            BehavioralEvents.SESSION_OVER -> true
            else -> false
        }
    }

    override fun handleEvent(event: AccessibilityEvent) {
        // Handle the swipe gesture event
        // Perform specific actions or processing for swipe gestures
        when(event.behavorType) {
            BehavioralEvents.SWIPE_EVENT -> scrollEvent(event)
            BehavioralEvents.SESSION_OVER -> sessionOverEvent()
            else -> {

            }
        }
    }

    private fun scrollEvent(event: AccessibilityEvent) {
        val convertedEvent = event.toScrollInterval()

        if(swIntervals.isNotEmpty()) {
            if(!swIntervals.peek().isNullOrEmpty()
                && (swIntervals.peek()?.first()?.apk ?: "") == convertedEvent.apk
            ){
                swIntervals.peek()?.add(convertedEvent)
            }else{
                swIntervals.add(mutableListOf(convertedEvent))
            }
        }else{
            swIntervals.add(mutableListOf(convertedEvent))
        }
    }

    private fun AccessibilityEvent.toScrollInterval() : ScrollEventInterval {
        return ScrollEventInterval(
            timestamp = this.eventTime ?: 0L,
            intervalId = TouchAccessibilityService.appIntervalId,
            apk = this.packageName.toString(),
            scrollDeltaX = this.scrollDeltaX,
            scrollDeltaY = this.scrollDeltaY
        )
    }

    private fun appChangeEvent(){

    }

    private fun sessionOverEvent(){
        swIntervals.forEach { sei ->
            swSegments.add(ScrollEventSegment(
                timestamp = sei.first().timestamp,
                scrollCount = sei.count(),
                sessionId = "",
                intervalId = sei.first().intervalId,
                apk = sei.first().apk,
                scrollDeltaXTotal = sei.filter { it.scrollDeltaX > 1 }.sumOf { abs(it.scrollDeltaX) },
                scrollDeltaYTotal = sei.filter { it.scrollDeltaY > 1 }.sumOf { abs(it.scrollDeltaY) }
            ))
        }

        save(generalOperations)
    }

    fun save(generalOperations: GenOp? = null) {
        generalOperations?.saveScrollEvents(swSegments)
    }

    private fun clear() {
        swIntervals.clear()
        swSegments.clear()
    }
}