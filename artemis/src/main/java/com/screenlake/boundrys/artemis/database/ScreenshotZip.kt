package com.screenlake.boundrys.artemis.database

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "screenshot_zip_table")
data class ScreenshotZip(
    var user: String? = null,
    var timestamp: String? = null,
    var localTimeStamp: String? = null,
    var file: String? = null,
    var toDelete: Boolean? = null,
    var panel_id: String? = null,
    var panel_name: String? = null,
    var tenant_id: String? = null
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}