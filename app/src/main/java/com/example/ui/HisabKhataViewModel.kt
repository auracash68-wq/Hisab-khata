package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import com.example.data.ExpenseRepository
import com.example.data.UserPreferencesRepository
import com.example.ui.strings.AppLanguage
import com.example.ui.strings.LocalizedStrings
import com.example.ui.strings.stringsFor
import com.example.util.CategorySummary
import com.example.util.CurrencyFormatter
import com.example.util.PdfGenerationResult
import com.example.util.PdfReportGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.math.roundToInt

enum class AnalyticsFilter {
    THIS_MONTH,
    LAST_MONTH,
    THIS_YEAR,
    CUSTOM_MONTH
}

data class CustomMonthSelection(
    val year: Int,
    val monthZeroBased: Int,
    val label: String
)

data class HisabKhataUiState(
    val isReady: Boolean = false,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val strings: LocalizedStrings = LocalizedStrings(),
    val allExpenses: List<ExpenseEntity> = emptyList(),
    val todaysTopThree: List<ExpenseEntity> = emptyList(),
    val totalSpentThisMonth: Int = 0,
    val todayTotal: Int = 0,
    val hasUnreadAlert: Boolean = true,
    val analyticsFilter: AnalyticsFilter = AnalyticsFilter.THIS_MONTH,
    val customMonth: CustomMonthSelection? = null,
    val analyticsExpenses: List<ExpenseEntity> = emptyList(),
    val analyticsTotal: Int = 0,
    val categorySummaries: List<CategorySummary> = emptyList(),
    val lastBackupMillis: Long? = null,
    val statusMessage: String? = null,
    val latestPdfResult: PdfGenerationResult? = null
)

class HisabKhataViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val prefsRepository = UserPreferencesRepository(application)
    private val repository = ExpenseRepository(database.expenseDao(), prefsRepository)

    private val isSeededFlow = MutableStateFlow(false)
    private val filterFlow = MutableStateFlow(AnalyticsFilter.THIS_MONTH)
    private val customMonthFlow = MutableStateFlow<CustomMonthSelection?>(null)
    private val statusMessageFlow = MutableStateFlow<String?>(null)
    private val latestPdfFlow = MutableStateFlow<PdfGenerationResult?>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureSeeded()
            isSeededFlow.value = true
        }
    }

    val uiState: StateFlow<HisabKhataUiState> = combine(
        repository.allExpenses,
        prefsRepository.languageFlow,
        prefsRepository.hasUnreadAlertFlow,
        prefsRepository.lastBackupMillisFlow,
        filterFlow,
        customMonthFlow,
        isSeededFlow,
        statusMessageFlow,
        latestPdfFlow
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val expenses = values[0] as List<ExpenseEntity>
        val language = values[1] as AppLanguage
        val unread = values[2] as Boolean
        val lastBackup = values[3] as Long?
        val filter = values[4] as AnalyticsFilter
        val customMonth = values[5] as CustomMonthSelection?
        val seeded = values[6] as Boolean
        val message = values[7] as String?
        val pdfResult = values[8] as PdfGenerationResult?

        val now = System.currentTimeMillis()
        val todayList = expenses.filter { CurrencyFormatter.isToday(it.timestampMillis, now) }
        val topThreeForHome = if (todayList.size >= 3) {
            todayList.take(3)
        } else {
            (todayList + expenses.filterNot { todayList.contains(it) }).take(3)
        }
        val thisMonthList = expenses.filter { CurrencyFormatter.isSameMonth(it.timestampMillis, 0, now) }

        val filteredAnalytics = when (filter) {
            AnalyticsFilter.THIS_MONTH -> thisMonthList
            AnalyticsFilter.LAST_MONTH -> expenses.filter { CurrencyFormatter.isSameMonth(it.timestampMillis, -1, now) }
            AnalyticsFilter.THIS_YEAR -> expenses.filter { CurrencyFormatter.isSameYear(it.timestampMillis, 0, now) }
            AnalyticsFilter.CUSTOM_MONTH -> {
                if (customMonth == null) thisMonthList
                else expenses.filter { item ->
                    val cal = Calendar.getInstance().apply { timeInMillis = item.timestampMillis }
                    cal.get(Calendar.YEAR) == customMonth.year &&
                        cal.get(Calendar.MONTH) == customMonth.monthZeroBased
                }
            }
        }

        val analyticsTotal = filteredAnalytics.sumOf { it.amount }
        val summaries = computeCategorySummaries(filteredAnalytics, analyticsTotal)

        HisabKhataUiState(
            isReady = seeded,
            language = language,
            strings = stringsFor(language),
            allExpenses = expenses,
            todaysTopThree = topThreeForHome,
            totalSpentThisMonth = thisMonthList.sumOf { it.amount },
            todayTotal = topThreeForHome.sumOf { it.amount },
            hasUnreadAlert = unread,
            analyticsFilter = filter,
            customMonth = customMonth,
            analyticsExpenses = filteredAnalytics,
            analyticsTotal = analyticsTotal,
            categorySummaries = summaries,
            lastBackupMillis = lastBackup,
            statusMessage = message,
            latestPdfResult = pdfResult
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HisabKhataUiState()
    )

    private fun computeCategorySummaries(
        expenses: List<ExpenseEntity>,
        total: Int
    ): List<CategorySummary> {
        val rawTotals = ExpenseCategory.entries.map { cat ->
            expenses.filter { ExpenseCategory.fromKey(it.category) == cat }.sumOf { it.amount }
        }
        if (total <= 0) {
            return ExpenseCategory.entries.map { CategorySummary(it, 0, 0) }
        }

        // Largest-remainder percentage rounding so slices always sum cleanly to 100%
        val exact = rawTotals.map { it * 100.0 / total }
        val floors = exact.map { it.toInt() }.toMutableList()
        var remainder = 100 - floors.sum()
        val remaindersOrder = exact.indices.sortedByDescending { exact[it] - floors[it] }
        var idx = 0
        while (remainder > 0 && idx < remaindersOrder.size) {
            val targetIdx = remaindersOrder[idx]
            if (rawTotals[targetIdx] > 0) {
                floors[targetIdx] = floors[targetIdx] + 1
                remainder--
            }
            idx++
        }

        return ExpenseCategory.entries.mapIndexed { index, category ->
            CategorySummary(
                category = category,
                amount = rawTotals[index],
                percentage = floors[index].coerceIn(0, 100)
            )
        }
    }

    fun selectAnalyticsFilter(filter: AnalyticsFilter) {
        filterFlow.value = filter
        if (filter != AnalyticsFilter.CUSTOM_MONTH) {
            customMonthFlow.value = null
        }
    }

    fun selectCustomMonth(year: Int, monthZeroBased: Int, label: String) {
        customMonthFlow.value = CustomMonthSelection(year, monthZeroBased, label)
        filterFlow.value = AnalyticsFilter.CUSTOM_MONTH
    }

    fun addExpense(category: ExpenseCategory, title: String, amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addExpense(category, title, amount)
            statusMessageFlow.value = "Added ${CurrencyFormatter.formatRupees(amount)} for ${category.displayName}"
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteExpense(id)
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            prefsRepository.setHasUnreadAlert(false)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch(Dispatchers.IO) {
            prefsRepository.setLanguage(language)
            statusMessageFlow.value = "Language set to ${language.displayName}"
        }
    }

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAll()
            statusMessageFlow.value = "All expense data deleted"
        }
    }

    fun exportPdfReport(context: Context) {
        val snapshot = uiState.value
        val periodLabel = when (snapshot.analyticsFilter) {
            AnalyticsFilter.THIS_MONTH -> "This Month"
            AnalyticsFilter.LAST_MONTH -> "Last Month"
            AnalyticsFilter.THIS_YEAR -> "This Year"
            AnalyticsFilter.CUSTOM_MONTH -> snapshot.customMonth?.label ?: "Selected Month"
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = PdfReportGenerator.generateExpenseReport(
                    context = context.applicationContext,
                    periodLabel = periodLabel,
                    totalAmount = snapshot.analyticsTotal,
                    summaries = snapshot.categorySummaries,
                    expenses = snapshot.analyticsExpenses
                )
                latestPdfFlow.value = result
            } catch (e: Exception) {
                statusMessageFlow.value = "Unable to create PDF: ${e.message ?: "Error"}"
            }
        }
    }

    fun exportBackupToUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val json = repository.exportBackupJson()
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(json.toByteArray(Charsets.UTF_8))
                    }
                }
                statusMessageFlow.value = "Backup saved safely"
            } catch (e: Exception) {
                statusMessageFlow.value = "Backup failed: ${e.message ?: "Error"}"
            }
        }
    }

    fun restoreBackupFromUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonText = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.bufferedReader().readText()
                } ?: ""
                val count = repository.importBackupJson(jsonText)
                statusMessageFlow.value = if (count > 0) {
                    "Restored $count expenses"
                } else {
                    "No valid expenses found in backup"
                }
            } catch (e: Exception) {
                statusMessageFlow.value = "Restore failed: ${e.message ?: "Invalid file"}"
            }
        }
    }

    fun clearStatusMessage() {
        statusMessageFlow.value = null
    }

    fun clearLatestPdfResult() {
        latestPdfFlow.value = null
    }
}
