package com.techvipin130524.splitmate.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techvipin130524.splitmate.data.local.dao.ActivityLogDao
import com.techvipin130524.splitmate.data.mapper.toDomain
import com.techvipin130524.splitmate.data.mapper.toEntity
import com.techvipin130524.splitmate.domain.model.ActivityLog
import com.techvipin130524.splitmate.domain.repository.ActivityLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ActivityLogRepositoryImpl @Inject constructor(
    private val activityLogDao: ActivityLogDao,
    private val firestore: FirebaseFirestore
) : ActivityLogRepository {

    override fun observeAllActivityLogs(): Flow<List<ActivityLog>> {
        return activityLogDao.observeAllActivityLogs().map { list -> list.map { it.toDomain() } }
    }

    override fun observeActivityLogsForGroup(groupId: String): Flow<List<ActivityLog>> {
        return activityLogDao.observeActivityLogsForGroup(groupId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun logActivity(activityLog: ActivityLog): Result<Unit> {
        return try {
            val id = if (activityLog.id.isEmpty()) UUID.randomUUID().toString() else activityLog.id
            val finalLog = activityLog.copy(id = id)

            activityLogDao.insertActivityLog(finalLog.toEntity())
            firestore.collection("activity_logs").document(id).set(finalLog).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
