package com.techvipin130524.splitmate.domain.usecase.split

import com.techvipin130524.splitmate.domain.model.ExpenseParticipant
import com.techvipin130524.splitmate.domain.model.SplitType
import kotlin.math.abs
import kotlin.math.roundToLong

object SplitCalculator {

    fun calculateEqualSplit(
        totalAmountPaise: Long,
        participants: List<Pair<String, String>> // Pair(userId, userName)
    ): List<ExpenseParticipant> {
        if (participants.isEmpty()) return emptyList()

        val count = participants.size
        val baseShare = totalAmountPaise / count
        var remainder = totalAmountPaise % count

        return participants.map { (userId, userName) ->
            val extra = if (remainder > 0) {
                remainder--
                1L
            } else 0L

            ExpenseParticipant(
                expenseId = "",
                userId = userId,
                userName = userName,
                splitType = SplitType.EQUAL,
                amount = baseShare + extra,
                percentage = 100.0 / count,
                shares = 1
            )
        }
    }

    fun calculateExactSplit(
        totalAmountPaise: Long,
        participants: List<Pair<String, String>>,
        exactAmountsPaise: Map<String, Long> // userId -> amount in paise
    ): Result<List<ExpenseParticipant>> {
        val sumExact = participants.sumOf { (userId, _) -> exactAmountsPaise[userId] ?: 0L }

        if (sumExact != totalAmountPaise) {
            val diff = totalAmountPaise - sumExact
            return Result.failure(
                IllegalArgumentException("Exact split amounts sum up to $sumExact paise, but total is $totalAmountPaise paise (diff: $diff)")
            )
        }

        val result = participants.map { (userId, userName) ->
            val amount = exactAmountsPaise[userId] ?: 0L
            val percentage = if (totalAmountPaise > 0) (amount.toDouble() / totalAmountPaise) * 100 else 0.0
            ExpenseParticipant(
                expenseId = "",
                userId = userId,
                userName = userName,
                splitType = SplitType.EXACT,
                amount = amount,
                percentage = percentage,
                shares = 1
            )
        }
        return Result.success(result)
    }

    fun calculatePercentageSplit(
        totalAmountPaise: Long,
        participants: List<Pair<String, String>>,
        percentages: Map<String, Double> // userId -> percentage
    ): Result<List<ExpenseParticipant>> {
        val totalPercentage = participants.sumOf { (userId, _) -> percentages[userId] ?: 0.0 }

        if (abs(totalPercentage - 100.0) > 0.01) {
            return Result.failure(
                IllegalArgumentException("Percentages must sum to 100% (current sum: $totalPercentage%)")
            )
        }

        var calculatedSum = 0L
        val participantList = participants.map { (userId, userName) ->
            val pct = percentages[userId] ?: 0.0
            val amount = (totalAmountPaise * pct / 100.0).roundToLong()
            calculatedSum += amount
            ExpenseParticipant(
                expenseId = "",
                userId = userId,
                userName = userName,
                splitType = SplitType.PERCENTAGE,
                amount = amount,
                percentage = pct,
                shares = 1
            )
        }.toMutableList()

        // Adjust rounding remainder on highest percentage participant
        val remainder = totalAmountPaise - calculatedSum
        if (remainder != 0L && participantList.isNotEmpty()) {
            val maxIndex = participantList.indices.maxByOrNull { participantList[it].percentage } ?: 0
            val maxPart = participantList[maxIndex]
            participantList[maxIndex] = maxPart.copy(amount = maxPart.amount + remainder)
        }

        return Result.success(participantList)
    }

    fun calculateSharesSplit(
        totalAmountPaise: Long,
        participants: List<Pair<String, String>>,
        sharesMap: Map<String, Int> // userId -> shares count
    ): Result<List<ExpenseParticipant>> {
        val totalShares = participants.sumOf { (userId, _) -> sharesMap[userId] ?: 0 }

        if (totalShares <= 0) {
            return Result.failure(IllegalArgumentException("Total shares must be greater than 0"))
        }

        var calculatedSum = 0L
        val participantList = participants.map { (userId, userName) ->
            val userShares = sharesMap[userId] ?: 0
            val amount = (totalAmountPaise * userShares.toDouble() / totalShares).roundToLong()
            calculatedSum += amount
            ExpenseParticipant(
                expenseId = "",
                userId = userId,
                userName = userName,
                splitType = SplitType.SHARES,
                amount = amount,
                percentage = (userShares.toDouble() / totalShares) * 100,
                shares = userShares
            )
        }.toMutableList()

        // Adjust rounding remainder
        val remainder = totalAmountPaise - calculatedSum
        if (remainder != 0L && participantList.isNotEmpty()) {
            val maxIndex = participantList.indices.maxByOrNull { participantList[it].shares } ?: 0
            val maxPart = participantList[maxIndex]
            participantList[maxIndex] = maxPart.copy(amount = maxPart.amount + remainder)
        }

        return Result.success(participantList)
    }
}
