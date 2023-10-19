package com.screenlake.boundrys.artemis.database
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.screenlake.boundrys.artemis.behavior.behaviors.models.common.BehavioralEvents
import java.util.*

@Entity(tableName = "accessibility_event_fts")
@Fts4(contentEntity = AccessibilityEvent::class)
data class AccessibilityEventFts(
    @ColumnInfo(name = "text") val text: String
)

@Entity(tableName = "accessibility_event")
data class AccessibilityEvent(
    var user: String? = null,
    var eventGroupId: String? = null,
    var sessionId: String? = null,
    var accessibilitySessionId: String? = null,
    var appIntervalId: String? = null,
    var eventType: String? = "ROOT",
    var eventTime: Long? = null,
    @SerializedName("PackageName")
    var packageName: String? = null,
    @SerializedName("ClassName")
    var className: String? = null,
    @SerializedName("Text")
    @ColumnInfo(name = "text")
    var text: String? = null,
    @SerializedName("ContentDescription")
    var contentDescription: String? = null,
    @SerializedName("Password")
    var password: Boolean? = null,
    @SerializedName("IsFocused")
    var isFocused: Boolean? = null,
    @SerializedName("ScrollDeltaX")
    var scrollDeltaX: Int = -1,
    @SerializedName("ScrollDeltaY")
    var scrollDeltaY: Int = -1,
    var r1: Int? = -1,
    var r2: Int? = -1,
    var r3: Int? = -1,
    var r4: Int? = -1,
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @Transient
    var behavorType: BehavioralEvents? = BehavioralEvents.NONE
}