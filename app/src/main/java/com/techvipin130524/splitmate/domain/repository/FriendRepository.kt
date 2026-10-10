package com.techvipin130524.splitmate.domain.repository

import com.techvipin130524.splitmate.domain.model.Friend
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    fun observeFriends(userId: String): Flow<List<Friend>>
    suspend fun sendFriendRequest(currentUserId: String, currentUserName: String, currentUserEmail: String, targetEmail: String): Result<Unit>
    suspend fun removeFriend(friendId: String): Result<Unit>
}
