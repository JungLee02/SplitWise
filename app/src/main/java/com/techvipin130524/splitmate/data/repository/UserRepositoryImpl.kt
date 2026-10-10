package com.techvipin130524.splitmate.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techvipin130524.splitmate.data.local.dao.UserDao
import com.techvipin130524.splitmate.data.mapper.toDomain
import com.techvipin130524.splitmate.data.mapper.toEntity
import com.techvipin130524.splitmate.domain.model.User
import com.techvipin130524.splitmate.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun observeUser(userId: String): Flow<User?> {
        return userDao.observeUser(userId).map { entity -> entity?.toDomain() }
    }

    override suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val local = userDao.getUser(userId)
            if (local != null) {
                Result.success(local.toDomain())
            } else {
                val snapshot = firestore.collection("users").document(userId).get().await()
                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    userDao.insertUser(user.toEntity())
                    Result.success(user)
                } else {
                    Result.failure(NoSuchElementException("User not found"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createOrUpdateUser(user: User): Result<Unit> {
        return try {
            userDao.insertUser(user.toEntity())
            firestore.collection("users").document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchUsers(query: String): Result<List<User>> {
        return try {
            val trimmed = query.trim().lowercase()
            if (trimmed.isEmpty()) return Result.success(emptyList())

            val snapshot = firestore.collection("users")
                .whereGreaterThanOrEqualTo("email", trimmed)
                .whereLessThanOrEqualTo("email", trimmed + "\uf8ff")
                .get()
                .await()

            val users = snapshot.toObjects(User::class.java)
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
