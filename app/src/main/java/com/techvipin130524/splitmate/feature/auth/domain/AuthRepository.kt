package com.techvipin130524.splitmate.feature.auth.domain

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthUser?>
    suspend fun login(
        email: String,
        password: String
    ): Result<AuthUser>

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<AuthUser>

    suspend fun resetPassword(
        email: String
    ): Result<Unit>

    fun logout()


}