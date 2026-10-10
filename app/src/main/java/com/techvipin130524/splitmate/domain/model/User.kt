package com.techvipin130524.splitmate.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
