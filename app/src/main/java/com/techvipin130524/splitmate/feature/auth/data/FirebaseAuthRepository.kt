package com.techvipin130524.splitmate.feature.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.techvipin130524.splitmate.feature.auth.domain.AuthRepository
import com.techvipin130524.splitmate.feature.auth.domain.AuthUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: com.google.firebase.firestore.FirebaseFirestore
) : AuthRepository {

    override val authState: Flow<AuthUser?> = callbackFlow {

        val listener = FirebaseAuth.AuthStateListener { auth ->

            trySend(
                auth.currentUser?.toAuthUser()
            )
        }

        firebaseAuth.addAuthStateListener(listener)

        // Emit current state immediately.
        trySend(
            firebaseAuth.currentUser?.toAuthUser()
        )

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthUser> {

        return try {

            val result = firebaseAuth
                .signInWithEmailAndPassword(
                    email,
                    password
                )
                .await()

            val user = result.user
                ?: return Result.failure(
                    IllegalStateException("User is null")
                )

            Result.success(
                user.toAuthUser()
            )

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<AuthUser> {

        return try {

            val result = firebaseAuth
                .createUserWithEmailAndPassword(
                    email,
                    password
                )
                .await()

            val user = result.user
                ?: return Result.failure(
                    IllegalStateException("User is null")
                )

            // Store display name in Firebase Authentication.
            val profileUpdate =
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

            user.updateProfile(profileUpdate).await()

            // Store user document in Firestore users collection
            val userProfile = com.techvipin130524.splitmate.domain.model.User(
                id = user.uid,
                name = name,
                email = email.trim().lowercase(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            firestore.collection("users").document(user.uid).set(userProfile).await()

            Result.success(
                user.toAuthUser()
            )

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    override suspend fun resetPassword(
        email: String
    ): Result<Unit> {

        return try {

            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    private fun com.google.firebase.auth.FirebaseUser.toAuthUser():
            AuthUser {

        return AuthUser(
            uid = uid,
            name = displayName,
            email = email,
            photoUrl = photoUrl?.toString()
        )
    }
}