package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import com.example.ui.HisabKhataUiState
import com.example.ui.components.ExpenseItemCard
import com.example.ui.components.categoryLabel
import com.example.ui.theme.AppBackground
import com.example.ui.theme.CardWhite
import com.example.ui.theme.DangerRed
import com.example.ui.theme.HeaderNavy
import com.example.ui.theme.PoppinsFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoFamily
import com.example.ui.theme.SecondaryText
import com.example.util.CurrencyFormatter

@Composable
fun AllExpensesScreen(
    uiState: HisabKhataUiState,
    onBackClick: () -> Unit,
    onDeleteExpense: (Long) -> Unit
) {
    BackHandler(onBack = onBackClick)

    val strings = uiState.strings
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var selectedExpenseForAction by remember { mutableStateOf<ExpenseEntity?>(null) }

    val filtered = remember(uiState.allExpenses, selectedCategory) {
        if (selectedCategory == null) uiState.allExpenses
        else uiState.allExpenses.filter { ExpenseCategory.fromKey(it.category) == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(PrimaryNavy, HeaderNavy)))
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .testTag("all_expenses_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = Color.White
                )
            }
            Text(
                text = strings.allExpensesTitle,
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All", fontFamily = RobotoFamily) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = CardWhite,
                            labelColor = PrimaryText
                        )
                    )
                }
                items(ExpenseCategory.entries) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(strings.categoryLabel(cat), fontFamily = RobotoFamily) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = cat.primaryColor,
                            selectedLabelColor = Color.White,
                            containerColor = CardWhite,
                            labelColor = PrimaryText
                        )
                    )
                }
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.noExpensesYet,
                        color = SecondaryText,
                        fontFamily = RobotoFamily,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { expense ->
                        ExpenseItemCard(
                            expense = expense,
                            strings = strings,
                            onClick = { selectedExpenseForAction = expense }
                        )
                    }
                }
            }
        }
    }

    selectedExpenseForAction?.let { item ->
        val cat = ExpenseCategory.fromKey(item.category)
        AlertDialog(
            onDismissRequest = { selectedExpenseForAction = null },
            containerColor = CardWhite,
            title = {
                Text(
                    text = "${strings.categoryLabel(cat)} • ${CurrencyFormatter.formatRupees(item.amount)}",
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "${item.title}\n${CurrencyFormatter.formatDateTime(item.timestampMillis)}",
                    color = SecondaryText,
                    fontFamily = RobotoFamily
                )
            },
            confirmButton = {
                TextButton(onClick = { selectedExpenseForAction = null }) {
                    Text("Close", color = PrimaryBlue, fontFamily = PoppinsFamily)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDeleteExpense(item.id)
                        selectedExpenseForAction = null
                    }
                ) {
                    Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = DangerRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", color = DangerRed, fontFamily = PoppinsFamily)
                }
            }
        )
    }
}
