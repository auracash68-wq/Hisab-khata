package com.example

import com.example.data.ExpenseCategory
import com.example.util.CurrencyFormatter
import com.example.util.VoiceExpenseParser
import org.junit.Assert.assertEquals
import org.junit.Test

class HisabKhataUnitTest {

    @Test
    fun currencyFormatter_formatsIndianRupees() {
        assertEquals("₹5,400", CurrencyFormatter.formatRupees(5400))
        assertEquals("₹2,268", CurrencyFormatter.formatRupees(2268))
        assertEquals("₹250", CurrencyFormatter.formatRupees(250))
    }

    @Test
    fun voiceExpenseParser_extractsFoodTravelGroceries() {
        val food = VoiceExpenseParser.parse("Spent 250 on lunch at restaurant")
        assertEquals(250, food.amount)
        assertEquals(ExpenseCategory.FOOD, food.category)

        val travel = VoiceExpenseParser.parse("120 bus ticket")
        assertEquals(120, travel.amount)
        assertEquals(ExpenseCategory.TRAVEL, travel.category)

        val groceries = VoiceExpenseParser.parse("180 rupees for vegetables and fruits")
        assertEquals(180, groceries.amount)
        assertEquals(ExpenseCategory.GROCERIES, groceries.category)
    }
}
