package com.techvipin130524.splitmate.domain.model

data class Settlement(
    val id: String = "",
    val groupId: String = "",
    val fromUserId: String = "",
    val fromUserName: String = "",
    val toUserId: String = "",
    val toUserName: String = "",
    val amount: Long = 0L, // In paise
    val currency: String = "INR",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
