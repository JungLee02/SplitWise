package com.techvipin130524.splitmate.domain.repository

import com.techvipin130524.splitmate.domain.model.Settlement
import kotlinx.coroutines.flow.Flow

interface SettlementRepository {
    fun observeSettlementsForGroup(groupId: String): Flow<List<Settlement>>
    fun observeAllSettlements(): Flow<List<Settlement>>
    suspend fun recordSettlement(settlement: Settlement): Result<Unit>
}
