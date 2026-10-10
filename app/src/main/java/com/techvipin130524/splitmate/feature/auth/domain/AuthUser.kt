package com.techvipin130524.splitmate.feature.auth.domain

data class AuthUser(
    val uid: String,
    val name: String?,
    val email: String?,
    val photoUrl: String?
)