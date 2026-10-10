package com.techvipin130524.splitmate.domain.usecase.split

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SplitCalculatorTest {

    @Test
    fun equalSplit_dividesEvenlyAndDistributesRemainder() {
        // Total = ₹10.00 (1000 paise), 3 participants
        val participants = listOf(
            "u1" to "Alice",
            "u2" to "Bob",
            "u3" to "Charlie"
        )

        val result = SplitCalculator.calculateEqualSplit(1000L, participants)

        assertEquals(3, result.size)
        val sum = result.sumOf { it.amount }
        assertEquals(1000L, sum)
        // 1000 / 3 = 333, remainder 1 -> first participant gets 334
        assertEquals(334L, result[0].amount)
        assertEquals(333L, result[1].amount)
        assertEquals(333L, result[2].amount)
    }

    @Test
    fun exactSplit_validatesTotalSum() {
        val participants = listOf("u1" to "Alice", "u2" to "Bob")
        val exactAmounts = mapOf("u1" to 600L, "u2" to 400L)

        val result = SplitCalculator.calculateExactSplit(1000L, participants, exactAmounts)
        assertTrue(result.isSuccess)
        assertEquals(1000L, result.getOrNull()?.sumOf { it.amount })

        // Invalid exact amounts sum
        val invalidAmounts = mapOf("u1" to 500L, "u2" to 400L)
        val invalidResult = SplitCalculator.calculateExactSplit(1000L, participants, invalidAmounts)
        assertTrue(invalidResult.isFailure)
    }

    @Test
    fun percentageSplit_calculatesAmountsAndValidatesSum() {
        val participants = listOf("u1" to "Alice", "u2" to "Bob", "u3" to "Charlie")
        val percentages = mapOf("u1" to 20.0, "u2" to 30.0, "u3" to 50.0)

        val result = SplitCalculator.calculatePercentageSplit(1200L, participants, percentages)
        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!
        assertEquals(240L, list[0].amount)
        assertEquals(360L, list[1].amount)
        assertEquals(600L, list[2].amount)
        assertEquals(1200L, list.sumOf { it.amount })
    }

    @Test
    fun sharesSplit_calculatesProportionately() {
        val participants = listOf("u1" to "Alice", "u2" to "Bob", "u3" to "Charlie")
        val shares = mapOf("u1" to 1, "u2" to 2, "u3" to 3)

        val result = SplitCalculator.calculateSharesSplit(1200L, participants, shares)
        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!
        assertEquals(200L, list[0].amount)
        assertEquals(400L, list[1].amount)
        assertEquals(600L, list[2].amount)
        assertEquals(1200L, list.sumOf { it.amount })
    }
}
