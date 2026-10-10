package com.techvipin130524.splitmate.domain.repository

import com.techvipin130524.splitmate.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(userId: String): Flow<User?>
    suspend fun getUserProfile(userId: String): Result<User>
    suspend fun createOrUpdateUser(user: User): Result<Unit>
    suspend fun searchUsers(query: String): Result<List<User>>
}
