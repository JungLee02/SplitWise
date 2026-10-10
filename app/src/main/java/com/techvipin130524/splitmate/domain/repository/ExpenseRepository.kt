package com.techvipin130524.splitmate.domain.repository

import com.techvipin130524.splitmate.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpensesForGroup(groupId: String): Flow<List<Expense>>
    fun observeAllExpenses(): Flow<List<Expense>>
    suspend fun getExpenseDetails(expenseId: String): Result<Expense>
    suspend fun addExpense(expense: Expense): Result<Unit>
    suspend fun deleteExpense(expenseId: String): Result<Unit>
}
