package com.screenlake.boundrys.artemis.behavior.behaviors.models.doa

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.TopicSeenInterval

@Dao
interface TopicSeenDAO {
    /**
     * Update TopicSeenInterval.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(topicSeenInterval: TopicSeenInterval)

    /**
     * Update TopicSeenInterval.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(topicSeenIntervals: List<TopicSeenInterval>)

    /**
     * Delete all TopicIntervals.
     */
    @Query("DELETE FROM word_seen_table")
    suspend fun nukeTable()

//    /**
//     * Get all events by time interval.
//     */
//    @Query("SELECT * FROM scroll_event_segment_table WHERE timestamp BETWEEN :startTime AND :endTime")
//    suspend fun getScrollEventsWithinTimeInterval(startTime: Long, endTime: Long): List<ScrollEventSegment>
//
//    /**
//     * Get total of scroll grouped by apk.
//     */
//    @Query("SELECT apk, SUM(scrollTotal) AS total FROM scroll_event_segment_table WHERE timestamp >= :hoursBefore GROUP BY apk")
//    suspend fun getGroupedScrollTotals(hoursBefore: Long): List<GroupedScrollTotal>
}