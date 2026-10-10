package com.techvipin130524.splitmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settlements")
data class SettlementEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val toUserName: String,
    val amount: Long,
    val currency: String,
    val note: String,
    val createdAt: Long
)
