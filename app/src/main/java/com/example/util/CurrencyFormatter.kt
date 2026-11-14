package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CurrencyFormatter {
    private val indianFormat: NumberFormat by lazy {
        NumberFormat.getNumberInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }
    }

    fun formatRupees(amount: Int): String = "₹${indianFormat.format(amount)}"

    fun formatTime(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.US)
        return sdf.format(Date(timestampMillis))
    }

    fun formatDateTime(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.US)
        return sdf.format(Date(timestampMillis))
    }

    fun formatShortDate(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM", Locale.US)
        return sdf.format(Date(timestampMillis))
    }

    fun isToday(timestampMillis: Long, nowMillis: Long = System.currentTimeMillis()): Boolean {
        val c1 = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        val c2 = Calendar.getInstance().apply { timeInMillis = nowMillis }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }

    fun isSameMonth(timestampMillis: Long, targetMonthOffset: Int = 0, nowMillis: Long = System.currentTimeMillis()): Boolean {
        val c1 = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        val c2 = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            add(Calendar.MONTH, targetMonthOffset)
        }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
    }

    fun isSameYear(timestampMillis: Long, yearOffset: Int = 0, nowMillis: Long = System.currentTimeMillis()): Boolean {
        val c1 = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        val c2 = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            add(Calendar.YEAR, yearOffset)
        }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
    }
}
