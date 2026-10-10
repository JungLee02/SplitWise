package com.techvipin130524.splitmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techvipin130524.splitmate.data.local.entity.SettlementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettlementDao {

    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun observeSettlementsForGroup(groupId: String): Flow<List<SettlementEntity>>

    @Query("SELECT * FROM settlements ORDER BY createdAt DESC")
    fun observeAllSettlements(): Flow<List<SettlementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementEntity)
}
