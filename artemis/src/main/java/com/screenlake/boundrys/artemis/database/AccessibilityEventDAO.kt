package com.screenlake.boundrys.artemis.database

import androidx.room.*

@Dao
interface AccessibilityEventDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(accessibilityEvent: AccessibilityEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(accessibilityEvent: List<AccessibilityEvent>)

    @Query("SELECT * FROM accessibility_event ORDER BY eventTime DESC LIMIT :limit")
    suspend fun getAllAccessibilityEvents(limit: Int): List<AccessibilityEvent>

    @Query("delete from accessibility_event where id in (:idList)")
    suspend fun deleteAccessibilityEvents(idList: List<Int>)

    @Query("SELECT * FROM accessibility_event where accessibilitySessionId = :sessionId")
    suspend fun getAllAccessibilityEventsBySessionId(sessionId: String): List<AccessibilityEvent>

    @Query("SELECT * FROM accessibility_event WHERE text MATCH :query")
    fun search(query: String): List<AccessibilityEvent>

    /**
     * Delete AccessibilityEvent
     */
    @Query("DELETE FROM accessibility_event")
    suspend fun deleteAccessibilityEvents()
}