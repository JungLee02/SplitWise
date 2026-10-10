package com.techvipin130524.splitmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techvipin130524.splitmate.data.local.entity.ExpenseEntity
import com.techvipin130524.splitmate.data.local.entity.ExpenseParticipantEntity
import com.techvipin130524.splitmate.data.local.entity.ExpensePayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE groupId = :groupId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun observeExpensesForGroup(groupId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun observeAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: String): ExpenseEntity?

    @Query("SELECT * FROM expense_participants WHERE expenseId = :expenseId")
    suspend fun getParticipantsForExpense(expenseId: String): List<ExpenseParticipantEntity>

    @Query("SELECT * FROM expense_payers WHERE expenseId = :expenseId")
    suspend fun getPayersForExpense(expenseId: String): List<ExpensePayerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<ExpenseParticipantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayers(payers: List<ExpensePayerEntity>)

    @Query("UPDATE expenses SET isDeleted = 1 WHERE id = :expenseId")
    suspend fun deleteExpense(expenseId: String): Int
}
