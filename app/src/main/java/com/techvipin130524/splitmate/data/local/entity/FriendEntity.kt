package com.techvipin130524.splitmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val friendUserId: String,
    val friendName: String,
    val friendEmail: String,
    val friendProfileImageUrl: String?,
    val status: String,
    val createdAt: Long
)
