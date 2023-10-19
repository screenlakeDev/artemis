package com.screenlake.boundrys.artemis.database

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
@Keep
@Entity(tableName = "user_table")
data class User(
    @SerializedName(value = "id")
    @Expose var id_dynanmo: String? = null,
    @Expose var createdAt: String? = null,
    @Expose var email: String? = null,
    @Expose var email_hash: String? = null,
    @Expose var tenant_id: String? = null,
    @Expose var tenant_name: String? = null,
    @Expose var updatedAt: String? = null,
    @Expose var username: String? = null,
    @Expose var created_timestamp: String? = null,
    @Expose var panel_id: String? = null,
    @Expose var panel_name: String? = null,
    @Expose var _lastChangedAt: String? = null,
    @Expose var _version: String? = null,
    @Expose var __typename: String? = null,
    @Expose var sdk: String? = null,
    @Expose var device: String? = null,
    @Expose var model: String? = null,
    @Expose var product: String? = null,
    @Expose var upload_images: Boolean = false,
    @Expose var is_emulator: Boolean? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

