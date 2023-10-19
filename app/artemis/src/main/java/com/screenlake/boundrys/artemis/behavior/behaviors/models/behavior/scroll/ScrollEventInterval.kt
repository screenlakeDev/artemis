package com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.scroll

import com.google.gson.annotations.SerializedName

data class ScrollEventInterval(
    var timestamp: Long,
    var intervalId: String,
    var apk: String,
    var scrollDeltaX: Int = -1,
    var scrollDeltaY: Int = -1,
)
