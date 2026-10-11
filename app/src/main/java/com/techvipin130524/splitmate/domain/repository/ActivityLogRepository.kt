package com.techvipin130524.splitmate.domain.repository

import com.techvipin130524.splitmate.domain.model.ActivityLog
import kotlinx.coroutines.flow.Flow

interface ActivityLogRepository {
    fun observeAllActivityLogs(): Flow<List<ActivityLog>>
    fun observeActivityLogsForGroup(groupId: String): Flow<List<ActivityLog>>
    suspend fun logActivity(activityLog: ActivityLog): Result<Unit>
}
