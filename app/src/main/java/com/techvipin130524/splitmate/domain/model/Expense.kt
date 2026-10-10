package com.techvipin130524.splitmate.domain.model

enum class SplitType {
    EQUAL,
    EXACT,
    PERCENTAGE,
    SHARES
}

data class Expense(
    val id: String = "",
    val groupId: String = "",
    val description: String = "",
    val totalAmount: Long = 0L, // Amount in minor units (paise)
    val currency: String = "INR",
    val createdBy: String = "",
    val createdByName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val participants: List<ExpenseParticipant> = emptyList(),
    val payers: List<ExpensePayer> = emptyList()
)

data class ExpenseParticipant(
    val id: String = "",
    val expenseId: String = "",
    val userId: String = "",
    val userName: String = "",
    val splitType: SplitType = SplitType.EQUAL,
    val amount: Long = 0L, // In paise
    val percentage: Double = 0.0,
    val shares: Int = 1
)

data class ExpensePayer(
    val id: String = "",
    val expenseId: String = "",
    val userId: String = "",
    val userName: String = "",
    val amount: Long = 0L // In paise
)
