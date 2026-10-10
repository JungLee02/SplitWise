package com.techvipin130524.splitmate.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techvipin130524.splitmate.data.local.dao.ExpenseDao
import com.techvipin130524.splitmate.data.mapper.toDomain
import com.techvipin130524.splitmate.data.mapper.toEntity
import com.techvipin130524.splitmate.domain.model.Expense
import com.techvipin130524.splitmate.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val firestore: FirebaseFirestore
) : ExpenseRepository {

    override fun observeExpensesForGroup(groupId: String): Flow<List<Expense>> {
        return expenseDao.observeExpensesForGroup(groupId).map { list ->
            list.map { entity ->
                val participants = expenseDao.getParticipantsForExpense(entity.id).map { it.toDomain() }
                val payers = expenseDao.getPayersForExpense(entity.id).map { it.toDomain() }
                entity.toDomain(participants, payers)
            }
        }
    }

    override fun observeAllExpenses(): Flow<List<Expense>> {
        return expenseDao.observeAllExpenses().map { list ->
            list.map { entity ->
                val participants = expenseDao.getParticipantsForExpense(entity.id).map { it.toDomain() }
                val payers = expenseDao.getPayersForExpense(entity.id).map { it.toDomain() }
                entity.toDomain(participants, payers)
            }
        }
    }

    override suspend fun getExpenseDetails(expenseId: String): Result<Expense> {
        return try {
            val entity = expenseDao.getExpenseById(expenseId)
            if (entity != null) {
                val participants = expenseDao.getParticipantsForExpense(expenseId).map { it.toDomain() }
                val payers = expenseDao.getPayersForExpense(expenseId).map { it.toDomain() }
                Result.success(entity.toDomain(participants, payers))
            } else {
                val snapshot = firestore.collection("expenses").document(expenseId).get().await()
                val expense = snapshot.toObject(Expense::class.java)
                if (expense != null) {
                    saveLocalExpense(expense)
                    Result.success(expense)
                } else {
                    Result.failure(NoSuchElementException("Expense not found"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            val expenseId = if (expense.id.isEmpty()) UUID.randomUUID().toString() else expense.id
            val finalExpense = expense.copy(id = expenseId)

            saveLocalExpense(finalExpense)
            firestore.collection("expenses").document(expenseId).set(finalExpense).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            expenseDao.deleteExpense(expenseId)
            firestore.collection("expenses").document(expenseId).update("isDeleted", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveLocalExpense(expense: Expense) {
        expenseDao.insertExpense(expense.toEntity())
        expenseDao.insertParticipants(expense.participants.map { it.toEntity(expense.id) })
        expenseDao.insertPayers(expense.payers.map { it.toEntity(expense.id) })
    }
}
