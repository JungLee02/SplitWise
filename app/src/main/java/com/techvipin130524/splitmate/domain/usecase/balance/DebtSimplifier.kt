package com.techvipin130524.splitmate.domain.usecase.balance

import com.techvipin130524.splitmate.domain.model.Expense
import com.techvipin130524.splitmate.domain.model.Settlement
import kotlin.math.abs
import kotlin.math.min

data class DebtTransaction(
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val toUserName: String,
    val amount: Long // in paise
)

data class UserNetBalance(
    val userId: String,
    val userName: String,
    val netBalancePaise: Long // positive = owed money (creditor), negative = owes money (debtor)
)

object DebtSimplifier {

    fun calculateNetBalances(
        userNames: Map<String, String>, // userId -> userName
        expenses: List<Expense>,
        settlements: List<Settlement>
    ): List<UserNetBalance> {
        val balanceMap = mutableMapOf<String, Long>()

        // Initialize all users in userNames map
        userNames.keys.forEach { userId ->
            balanceMap[userId] = 0L
        }

        // Process expenses
        for (expense in expenses) {
            if (expense.isDeleted) continue

            // Payers
            if (expense.payers.isNotEmpty()) {
                for (payer in expense.payers) {
                    balanceMap[payer.userId] = (balanceMap[payer.userId] ?: 0L) + payer.amount
                }
            } else {
                balanceMap[expense.createdBy] = (balanceMap[expense.createdBy] ?: 0L) + expense.totalAmount
            }

            // Participants
            for (participant in expense.participants) {
                balanceMap[participant.userId] = (balanceMap[participant.userId] ?: 0L) - participant.amount
            }
        }

        // Process settlements
        for (settlement in settlements) {
            // fromUserId paid toUserId, so fromUserId's balance increases (paid debt), toUserId's balance decreases (received payment)
            balanceMap[settlement.fromUserId] = (balanceMap[settlement.fromUserId] ?: 0L) + settlement.amount
            balanceMap[settlement.toUserId] = (balanceMap[settlement.toUserId] ?: 0L) - settlement.amount
        }

        return balanceMap.map { (userId, balance) ->
            UserNetBalance(
                userId = userId,
                userName = userNames[userId] ?: userId,
                netBalancePaise = balance
            )
        }
    }

    fun simplifyDebts(
        netBalances: List<UserNetBalance>
    ): List<DebtTransaction> {
        // Creditors (+ balance) and Debtors (- balance)
        class Participant(val userId: String, val userName: String, var amount: Long)

        val creditors = netBalances
            .filter { it.netBalancePaise > 0 }
            .map { Participant(it.userId, it.userName, it.netBalancePaise) }
            .sortedByDescending { it.amount }
            .toMutableList()

        val debtors = netBalances
            .filter { it.netBalancePaise < 0 }
            .map { Participant(it.userId, it.userName, abs(it.netBalancePaise)) }
            .sortedByDescending { it.amount }
            .toMutableList()

        val transactions = mutableListOf<DebtTransaction>()

        var cIndex = 0
        var dIndex = 0

        while (cIndex < creditors.size && dIndex < debtors.size) {
            val creditor = creditors[cIndex]
            val debtor = debtors[dIndex]

            val settlementAmount = min(creditor.amount, debtor.amount)

            if (settlementAmount > 0) {
                transactions.add(
                    DebtTransaction(
                        fromUserId = debtor.userId,
                        fromUserName = debtor.userName,
                        toUserId = creditor.userId,
                        toUserName = creditor.userName,
                        amount = settlementAmount
                    )
                )

                creditor.amount -= settlementAmount
                debtor.amount -= settlementAmount
            }

            if (creditor.amount == 0L) cIndex++
            if (debtor.amount == 0L) dIndex++
        }

        return transactions
    }
}
