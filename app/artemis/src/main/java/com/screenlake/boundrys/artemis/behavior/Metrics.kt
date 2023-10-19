package com.screenlake.boundrys.artemis.behavior;

import android.content.Context
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.scroll.ScrollEventSegment
import com.screenlake.boundrys.artemis.behavior.behaviors.models.doa.ScrollEventlDAO
import com.screenlake.boundrys.artemis.repository.DatabaseModule
import java.lang.ref.WeakReference

class Metrics(private val context: WeakReference<Context>, doaRef: ScrollEventlDAO? = null) {
    private var dao: ScrollEventlDAO

    init {
        dao = doaRef ?: DatabaseModule.provideAppDatabase(context.get()!!).getScrollEventlDAO
    }

    suspend fun getGroupedTotals(hoursBefore:Long) =  dao.getGroupedScrollTotals(hoursBefore)

    suspend fun getGroupedEventsByTimeInterval(time: Long, testNow:Long? = null): Map<Long, List<ScrollEventSegment>> {
        val now = testNow ?: Utility.now()
        val events =  dao.getScrollEventsWithinTimeInterval(now - time, now)
        return events.groupEpochTimeByIntervals(Utility.getEpochTime("15_MINUTES"))
    }

    fun List<ScrollEventSegment>.groupEpochTimeByIntervals(intervalSeconds: Long): Map<Long, List<ScrollEventSegment>> {
        return this.groupBy { it.timestamp / intervalSeconds }
    }
}

