package com.techvipin130524.splitmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val groupName: String,
    val title: String,
    val description: String,
    val type: String,
    val timestamp: Long
)
