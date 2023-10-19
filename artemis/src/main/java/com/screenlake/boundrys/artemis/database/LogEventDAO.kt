package com.screenlake.boundrys.artemis.database

import androidx.room.*

@Dao
interface LogEventDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(log: LogEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSync(log: LogEvent)

    @Query("SELECT * FROM log_event_table ORDER BY timestamp DESC LIMIT :limit OFFSET :offset ")
    suspend fun getLogsFrom(limit: Int, offset: Int): List<LogEvent>

    @Query("SELECT Count(*) FROM log_event_table")
    suspend fun logCount(): Int

    @Query("delete from log_event_table where id in (:idList)")
    fun deleteLogEvents(idList: List<Int>)

    /**
     * Deleting logs
     */
    @Query("DELETE FROM log_event_table")
    suspend fun deleteLogs()
}