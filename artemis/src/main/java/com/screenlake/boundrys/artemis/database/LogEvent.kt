package com.screenlake.boundrys.artemis.database

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Keep
@Entity(tableName = "log_event_table")
class LogEvent(
    @Expose val event:String,
    @Expose var msg:String,
    @Expose val user:String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}