package com.techvipin130524.splitmate.domain.model

enum class FriendStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

data class Friend(
    val id: String = "",
    val userId: String = "",
    val friendUserId: String = "",
    val friendName: String = "",
    val friendEmail: String = "",
    val friendProfileImageUrl: String? = null,
    val status: FriendStatus = FriendStatus.ACCEPTED,
    val createdAt: Long = System.currentTimeMillis()
)
