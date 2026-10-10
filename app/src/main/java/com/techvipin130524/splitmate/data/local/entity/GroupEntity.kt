package com.techvipin130524.splitmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "group_members", primaryKeys = ["groupId", "userId"])
data class GroupMemberEntity(
    val groupId: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val role: String,
    val joinedAt: Long
)
