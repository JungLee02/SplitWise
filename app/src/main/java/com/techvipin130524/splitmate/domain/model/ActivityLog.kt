package com.techvipin130524.splitmate.domain.model

enum class ActivityType {
    EXPENSE_ADDED,
    SETTLEMENT_RECORDED,
    GROUP_CREATED,
    MEMBER_JOINED
}

data class ActivityLog(
    val id: String = "",
    val groupId: String = "",
    val groupName: String = "",
    val title: String = "",
    val description: String = "",
    val type: ActivityType = ActivityType.EXPENSE_ADDED,
    val timestamp: Long = System.currentTimeMillis()
)
