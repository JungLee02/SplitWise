package com.techvipin130524.splitmate.domain.model

data class Group(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val inviteCode: String = "",
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val members: List<GroupMember> = emptyList()
)

data class GroupMember(
    val groupId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val role: String = "MEMBER",
    val joinedAt: Long = System.currentTimeMillis()
)
