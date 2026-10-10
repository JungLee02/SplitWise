package com.techvipin130524.splitmate.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techvipin130524.splitmate.data.local.dao.SettlementDao
import com.techvipin130524.splitmate.data.mapper.toDomain
import com.techvipin130524.splitmate.data.mapper.toEntity
import com.techvipin130524.splitmate.domain.model.Settlement
import com.techvipin130524.splitmate.domain.repository.SettlementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class SettlementRepositoryImpl @Inject constructor(
    private val settlementDao: SettlementDao,
    private val firestore: FirebaseFirestore
) : SettlementRepository {

    override fun observeSettlementsForGroup(groupId: String): Flow<List<Settlement>> {
        return settlementDao.observeSettlementsForGroup(groupId).map { list -> list.map { it.toDomain() } }
    }

    override fun observeAllSettlements(): Flow<List<Settlement>> {
        return settlementDao.observeAllSettlements().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun recordSettlement(settlement: Settlement): Result<Unit> {
        return try {
            val settlementId = if (settlement.id.isEmpty()) UUID.randomUUID().toString() else settlement.id
            val finalSettlement = settlement.copy(id = settlementId)

            settlementDao.insertSettlement(finalSettlement.toEntity())
            firestore.collection("settlements").document(settlementId).set(finalSettlement).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
