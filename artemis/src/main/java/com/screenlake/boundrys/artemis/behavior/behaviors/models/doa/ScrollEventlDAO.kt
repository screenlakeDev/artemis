package com.screenlake.boundrys.artemis.behavior.behaviors.models.doa

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.scroll.GroupedScrollTotal
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.scroll.ScrollEventSegment

@Dao
interface ScrollEventlDAO {
    /**
     * Update ScrollEventSegment.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(scrollEventSegment: ScrollEventSegment)

    /**
     * Update ScrollEventSegment.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(scrollEventSegments: List<ScrollEventSegment>)

    /**
     * Delete all ScrollEventSegments.
     */
    @Query("DELETE FROM scroll_event_segment_table")
    suspend fun nukeTable()

    /**
     * Get all events by time interval.
     */
    @Query("SELECT * FROM scroll_event_segment_table WHERE timestamp BETWEEN :startTime AND :endTime")
    suspend fun getScrollEventsWithinTimeInterval(startTime: Long, endTime: Long): List<ScrollEventSegment>

    /**
     * Get total of scroll grouped by apk.
     */
    @Query("SELECT apk, SUM(scrollTotal) AS total FROM scroll_event_segment_table WHERE timestamp >= :hoursBefore GROUP BY apk")
    suspend fun getGroupedScrollTotals(hoursBefore: Long): List<GroupedScrollTotal>
}