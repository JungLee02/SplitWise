package com.techvipin130524.splitmate.domain.usecase.balance

import com.techvipin130524.splitmate.domain.model.Expense
import com.techvipin130524.splitmate.domain.model.ExpenseParticipant
import com.techvipin130524.splitmate.domain.model.ExpensePayer
import org.junit.Assert.assertEquals
import org.junit.Test

class DebtSimplifierTest {

    @Test
    fun calculateNetBalancesAndSimplifyDebts() {
        val userNames = mapOf("u1" to "Alice", "u2" to "Bob", "u3" to "Charlie")

        // Alice paid ₹1200 dinner for Alice (₹400), Bob (₹400), Charlie (₹400)
        val dinnerExpense = Expense(
            id = "e1",
            totalAmount = 1200L,
            createdBy = "u1",
            payers = listOf(ExpensePayer(userId = "u1", amount = 1200L)),
            participants = listOf(
                ExpenseParticipant(userId = "u1", amount = 400L),
                ExpenseParticipant(userId = "u2", amount = 400L),
                ExpenseParticipant(userId = "u3", amount = 400L)
            )
        )

        val balances = DebtSimplifier.calculateNetBalances(userNames, listOf(dinnerExpense), emptyList())

        val aliceBal = balances.find { it.userId == "u1" }?.netBalancePaise
        val bobBal = balances.find { it.userId == "u2" }?.netBalancePaise
        val charlieBal = balances.find { it.userId == "u3" }?.netBalancePaise

        assertEquals(800L, aliceBal)  // Alice paid 1200, share 400 -> +800
        assertEquals(-400L, bobBal)  // Bob share 400 -> -400
        assertEquals(-400L, charlieBal) // Charlie share 400 -> -400

        val transactions = DebtSimplifier.simplifyDebts(balances)

        assertEquals(2, transactions.size)
        val sumTransactions = transactions.sumOf { it.amount }
        assertEquals(800L, sumTransactions)
    }

    @Test
    fun debtSimplification_simplifiesChain() {
        // A owes B 500, B owes C 500 -> A owes C 500
        val netBalances = listOf(
            UserNetBalance("A", "Alice", -500L),
            UserNetBalance("B", "Bob", 0L),
            UserNetBalance("C", "Charlie", 500L)
        )

        val transactions = DebtSimplifier.simplifyDebts(netBalances)

        assertEquals(1, transactions.size)
        val tx = transactions[0]
        assertEquals("A", tx.fromUserId)
        assertEquals("C", tx.toUserId)
        assertEquals(500L, tx.amount)
    }
}
