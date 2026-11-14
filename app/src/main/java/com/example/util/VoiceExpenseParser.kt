package com.example.util

import com.example.data.ExpenseCategory

data class ParsedExpense(
    val amount: Int?,
    val category: ExpenseCategory,
    val title: String
)

object VoiceExpenseParser {
    private val foodKeywords = listOf(
        "food", "lunch", "dinner", "breakfast", "snack", "tea", "chai", "coffee",
        "restaurant", "hotel", "dhaba", "biryani", "pizza", "burger", "samosa",
        "thali", "meal", "tiffin", "eat", "khana", "roti", "dosa", "idli", "sweet"
    )
    private val travelKeywords = listOf(
        "travel", "bus", "auto", "rickshaw", "cab", "taxi", "uber", "ola", "rapido",
        "train", "metro", "ticket", "petrol", "diesel", "fuel", "flight", "toll",
        "parking", "ride", "commute", "fare", "journey"
    )
    private val groceryKeywords = listOf(
        "grocery", "groceries", "vegetable", "vegetables", "fruit", "fruits", "sabzi",
        "milk", "bread", "eggs", "rice", "dal", "atta", "flour", "oil", "sugar",
        "ration", "kirana", "supermarket", "dairy", "paneer", "curd", "butter"
    )

    fun parse(spokenText: String): ParsedExpense {
        val clean = spokenText.trim()
        if (clean.isEmpty()) {
            return ParsedExpense(amount = null, category = ExpenseCategory.FOOD, title = "")
        }

        val amountRegex = Regex("""(?:₹|rs\.?|inr|rupees?)?\s*(\d+(?:,\d{3})*(?:\.\d+)?)""", RegexOption.IGNORE_CASE)
        val match = amountRegex.find(clean)
        val parsedAmount = match?.groupValues?.getOrNull(1)
            ?.replace(",", "")
            ?.toDoubleOrNull()
            ?.toInt()
            ?.takeIf { it > 0 }

        val lower = clean.lowercase()
        val category = when {
            groceryKeywords.any { lower.contains(it) } -> ExpenseCategory.GROCERIES
            travelKeywords.any { lower.contains(it) } -> ExpenseCategory.TRAVEL
            foodKeywords.any { lower.contains(it) } -> ExpenseCategory.FOOD
            else -> ExpenseCategory.OTHERS
        }

        var title = clean
            .replace(Regex("""(?:₹|rs\.?|inr|rupees?)\s*\d+(?:,\d{3})*(?:\.\d+)?""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\b\d+(?:,\d{3})*(?:\.\d+)?\b"""), "")
            .replace(Regex("""\b(?:spent|spend|paid|pay|for|on|at|rupees|rs|inr|ka|ke|ki)\b""", RegexOption.IGNORE_CASE), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()

        if (title.isEmpty()) {
            title = category.displayName
        } else {
            title = title.split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }

        return ParsedExpense(
            amount = parsedAmount,
            category = category,
            title = title
        )
    }
}
