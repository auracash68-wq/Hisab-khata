package com.example.data

import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val prefsRepository: UserPreferencesRepository
) {
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    suspend fun ensureSeeded() {
        val existing = expenseDao.getAllExpensesOnce()
        if (!prefsRepository.hasSeededInitialData() || existing.isEmpty()) {
            if (existing.isEmpty()) {
                expenseDao.insertExpenses(buildInitialSeedExpenses())
            }
            prefsRepository.markSeeded()
        } else {
            val now = Calendar.getInstance()
            val y = now.get(Calendar.YEAR)
            val m = now.get(Calendar.MONTH)
            val d = now.get(Calendar.DAY_OF_MONTH)
            fun todayAt(h: Int, min: Int): Long = Calendar.getInstance().apply {
                set(y, m, d, h, min, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val referenceTodayItems = listOf(
                ExpenseEntity(
                    category = ExpenseCategory.FOOD.name,
                    title = "Lunch at Restaurant",
                    amount = 250,
                    timestampMillis = todayAt(20, 15)
                ),
                ExpenseEntity(
                    category = ExpenseCategory.TRAVEL.name,
                    title = "Bus Ticket",
                    amount = 120,
                    timestampMillis = todayAt(17, 42)
                ),
                ExpenseEntity(
                    category = ExpenseCategory.GROCERIES.name,
                    title = "Vegetables & Fruits",
                    amount = 180,
                    timestampMillis = todayAt(13, 20)
                )
            )
            referenceTodayItems.forEach { ref ->
                val match = existing.firstOrNull {
                    it.category == ref.category && it.title == ref.title && it.amount == ref.amount
                }
                if (match == null) {
                    expenseDao.insertExpense(ref)
                }
            }
        }
    }

    suspend fun addExpense(category: ExpenseCategory, title: String, amount: Int, timestampMillis: Long = System.currentTimeMillis()) {
        expenseDao.insertExpense(
            ExpenseEntity(
                category = category.name,
                title = title.trim().ifEmpty { category.displayName },
                amount = amount.coerceAtLeast(1),
                timestampMillis = timestampMillis
            )
        )
        prefsRepository.setHasUnreadAlert(true)
    }

    suspend fun deleteExpense(id: Long) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun clearAll() {
        expenseDao.clearAllExpenses()
        prefsRepository.markSeeded()
    }

    suspend fun exportBackupJson(): String {
        val list = expenseDao.getAllExpensesOnce()
        val root = JSONObject()
        root.put("app", "Hisab Khata")
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())
        val arr = JSONArray()
        list.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("category", item.category)
                    put("title", item.title)
                    put("amount", item.amount)
                    put("timestampMillis", item.timestampMillis)
                }
            )
        }
        root.put("expenses", arr)
        prefsRepository.setLastBackupMillis(System.currentTimeMillis())
        return root.toString(2)
    }

    suspend fun importBackupJson(jsonText: String): Int {
        val root = JSONObject(jsonText)
        val arr = root.getJSONArray("expenses")
        val imported = mutableListOf<ExpenseEntity>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val category = ExpenseCategory.fromKey(obj.optString("category", "OTHERS"))
            val title = obj.optString("title", category.displayName)
            val amount = obj.optInt("amount", 0)
            val ts = obj.optLong("timestampMillis", System.currentTimeMillis())
            if (amount > 0) {
                imported.add(
                    ExpenseEntity(
                        category = category.name,
                        title = title,
                        amount = amount,
                        timestampMillis = ts
                    )
                )
            }
        }
        if (imported.isNotEmpty()) {
            expenseDao.clearAllExpenses()
            expenseDao.insertExpenses(imported)
            prefsRepository.markSeeded()
        }
        return imported.size
    }

    /**
     * Seeds expenses so the initial state matches the exact reference totals:
     * - Today's Expenses (top 3):
     *   1. Food — Lunch at Restaurant — ₹250 — 8:15 PM
     *   2. Travel — Bus Ticket — ₹120 — 5:42 PM
     *   3. Groceries — Vegetables & Fruits — ₹180 — 1:20 PM
     * - This Month Total: ₹5,400
     *   Food: ₹2,268 (42%), Travel: ₹1,512 (28%), Groceries: ₹972 (18%), Others: ₹648 (12%)
     * - Plus realistic Last Month entries for functional time-filter switching.
     */
    private fun buildInitialSeedExpenses(): List<ExpenseEntity> {
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)
        val currentMonth = now.get(Calendar.MONTH)
        val currentDay = now.get(Calendar.DAY_OF_MONTH)

        fun todayAt(hour24: Int, minute: Int): Long {
            return Calendar.getInstance().apply {
                set(currentYear, currentMonth, currentDay, hour24, minute, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        fun earlierThisMonth(daysBack: Int, hour24: Int): Long {
            val targetDay = (currentDay - daysBack).coerceAtLeast(1)
            return Calendar.getInstance().apply {
                set(currentYear, currentMonth, targetDay, hour24, 15, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        fun lastMonthAt(day: Int, hour24: Int): Long {
            return Calendar.getInstance().apply {
                set(currentYear, currentMonth, 1, hour24, 30, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.MONTH, -1)
                val maxDay = getActualMaximum(Calendar.DAY_OF_MONTH)
                set(Calendar.DAY_OF_MONTH, day.coerceAtMost(maxDay))
            }.timeInMillis
        }

        return listOf(
            // Today's 3 reference items (newest first)
            ExpenseEntity(
                category = ExpenseCategory.FOOD.name,
                title = "Lunch at Restaurant",
                amount = 250,
                timestampMillis = todayAt(20, 15)
            ),
            ExpenseEntity(
                category = ExpenseCategory.TRAVEL.name,
                title = "Bus Ticket",
                amount = 120,
                timestampMillis = todayAt(17, 42)
            ),
            ExpenseEntity(
                category = ExpenseCategory.GROCERIES.name,
                title = "Vegetables & Fruits",
                amount = 180,
                timestampMillis = todayAt(13, 20)
            ),

            // Earlier this month: remaining amounts so This Month sums to exact reference breakdown:
            // Food = 250 + 2018 = 2268 (42%)
            // Travel = 120 + 1392 = 1512 (28%)
            // Groceries = 180 + 792 = 972 (18%)
            // Others = 648 (12%)
            // Total = 5400
            ExpenseEntity(
                category = ExpenseCategory.FOOD.name,
                title = "Family Dinner",
                amount = 1150,
                timestampMillis = earlierThisMonth(1, 12)
            ),
            ExpenseEntity(
                category = ExpenseCategory.FOOD.name,
                title = "Snacks & Chai",
                amount = 868,
                timestampMillis = earlierThisMonth(2, 11)
            ),
            ExpenseEntity(
                category = ExpenseCategory.TRAVEL.name,
                title = "Metro & Auto Pass",
                amount = 892,
                timestampMillis = earlierThisMonth(1, 10)
            ),
            ExpenseEntity(
                category = ExpenseCategory.TRAVEL.name,
                title = "Petrol Refill",
                amount = 500,
                timestampMillis = earlierThisMonth(3, 9)
            ),
            ExpenseEntity(
                category = ExpenseCategory.GROCERIES.name,
                title = "Monthly Ration & Rice",
                amount = 792,
                timestampMillis = earlierThisMonth(2, 8)
            ),
            ExpenseEntity(
                category = ExpenseCategory.OTHERS.name,
                title = "Mobile Recharge & Bills",
                amount = 648,
                timestampMillis = earlierThisMonth(4, 7)
            ),

            // Last month records so Last Month and This Year filters show distinct live calculations
            ExpenseEntity(
                category = ExpenseCategory.FOOD.name,
                title = "Tiffin & Dining",
                amount = 1800,
                timestampMillis = lastMonthAt(22, 19)
            ),
            ExpenseEntity(
                category = ExpenseCategory.TRAVEL.name,
                title = "Train & Cab Fare",
                amount = 1250,
                timestampMillis = lastMonthAt(16, 14)
            ),
            ExpenseEntity(
                category = ExpenseCategory.GROCERIES.name,
                title = "Dairy & Staples",
                amount = 1100,
                timestampMillis = lastMonthAt(10, 11)
            ),
            ExpenseEntity(
                category = ExpenseCategory.OTHERS.name,
                title = "Pharmacy & Utilities",
                amount = 850,
                timestampMillis = lastMonthAt(5, 16)
            )
        )
    }
}
