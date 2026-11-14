package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CategoryPurple
import com.example.ui.theme.IndianGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SaffronOrange

enum class ExpenseCategory(
    val displayName: String,
    val primaryColor: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val sideShadowColor: Color
) {
    FOOD(
        displayName = "Food",
        primaryColor = SaffronOrange,
        gradientStart = Color(0xFFFFA726),
        gradientEnd = Color(0xFFFF7A00),
        sideShadowColor = Color(0xFFC45B00)
    ),
    TRAVEL(
        displayName = "Travel",
        primaryColor = BrightBlue,
        gradientStart = Color(0xFF2994FF),
        gradientEnd = PrimaryBlue,
        sideShadowColor = Color(0xFF0A3EC2)
    ),
    GROCERIES(
        displayName = "Groceries",
        primaryColor = IndianGreen,
        gradientStart = Color(0xFF3CE028),
        gradientEnd = Color(0xFF13B300),
        sideShadowColor = Color(0xFF0B7D00)
    ),
    OTHERS(
        displayName = "Others",
        primaryColor = CategoryPurple,
        gradientStart = Color(0xFFA25BFF),
        gradientEnd = Color(0xFF7122D9),
        sideShadowColor = Color(0xFF4F13A3)
    );

    companion object {
        fun fromKey(key: String): ExpenseCategory =
            entries.firstOrNull { it.name.equals(key, ignoreCase = true) } ?: OTHERS
    }
}
