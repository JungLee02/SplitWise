package com.techvipin130524.splitmate.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techvipin130524.splitmate.data.local.dao.FriendDao
import com.techvipin130524.splitmate.data.mapper.toDomain
import com.techvipin130524.splitmate.data.mapper.toEntity
import com.techvipin130524.splitmate.domain.model.Friend
import com.techvipin130524.splitmate.domain.model.FriendStatus
import com.techvipin130524.splitmate.domain.model.User
import com.techvipin130524.splitmate.domain.repository.FriendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val friendDao: FriendDao,
    private val firestore: FirebaseFirestore
) : FriendRepository {

    override fun observeFriends(userId: String): Flow<List<Friend>> {
        return friendDao.observeFriends(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun sendFriendRequest(
        currentUserId: String,
        currentUserName: String,
        currentUserEmail: String,
        targetEmail: String
    ): Result<Unit> {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("email", targetEmail.trim().lowercase())
                .get()
                .await()

            val targetUser = querySnapshot.documents.firstOrNull()?.toObject(User::class.java)
                ?: return Result.failure(IllegalArgumentException("User with email $targetEmail not found"))

            if (targetUser.id == currentUserId) {
                return Result.failure(IllegalArgumentException("Cannot add yourself as friend"))
            }

            val friendId = UUID.randomUUID().toString()
            val friendForCurrent = Friend(
                id = friendId,
                userId = currentUserId,
                friendUserId = targetUser.id,
                friendName = targetUser.name,
                friendEmail = targetUser.email,
                friendProfileImageUrl = targetUser.profileImageUrl,
                status = FriendStatus.ACCEPTED
            )

            val friendForTarget = Friend(
                id = UUID.randomUUID().toString(),
                userId = targetUser.id,
                friendUserId = currentUserId,
                friendName = currentUserName,
                friendEmail = currentUserEmail,
                status = FriendStatus.ACCEPTED
            )

            friendDao.insertFriend(friendForCurrent.toEntity())
            firestore.collection("friends").document(friendForCurrent.id).set(friendForCurrent).await()
            firestore.collection("friends").document(friendForTarget.id).set(friendForTarget).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFriend(friendId: String): Result<Unit> {
        return try {
            friendDao.deleteFriend(friendId)
            firestore.collection("friends").document(friendId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
