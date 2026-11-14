package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ExpenseCategory
import com.example.ui.AnalyticsFilter
import com.example.ui.HisabKhataUiState
import com.example.ui.components.HisabBottomNavigationBar
import com.example.ui.components.MainNavTab
import com.example.ui.components.PieChart3D
import com.example.ui.components.categoryLabel
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CardShadowTint
import com.example.ui.theme.CardWhite
import com.example.ui.theme.DividerSubtle
import com.example.ui.theme.FilterInactiveText
import com.example.ui.theme.HeaderNavy
import com.example.ui.theme.PoppinsFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoFamily
import com.example.ui.theme.SecondaryText
import com.example.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AnalyticsScreen(
    uiState: HisabKhataUiState,
    onBackClick: () -> Unit,
    onNavigateTab: (MainNavTab) -> Unit,
    onSelectFilter: (AnalyticsFilter) -> Unit,
    onSelectCustomMonth: (Int, Int, String) -> Unit,
    onDownloadPdfClick: () -> Unit,
    onDismissPdfResult: () -> Unit
) {
    BackHandler(onBack = onBackClick)

    val context = LocalContext.current
    val strings = uiState.strings
    var selectedSliceCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var showCalendarDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryNavy, AppBackground, Color(0xFFEAF1FD))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dark navy upper region curving behind the top of the large white chart card
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(235.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(w, 0f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryNavy, HeaderNavy)
                        )
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .align(Alignment.TopCenter)
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    // Header row: Back | Analytics | Calendar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("analytics_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back),
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Text(
                            text = strings.analyticsTitle,
                            color = Color.White,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 21.5.sp
                        )

                        IconButton(
                            onClick = { showCalendarDialog = true },
                            modifier = Modifier.testTag("analytics_calendar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = stringResource(R.string.cd_calendar),
                                tint = Color.White,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Time Filter Controls: This Month | Last Month | This Year
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnalyticsFilterChip(
                            label = strings.thisMonth,
                            selected = uiState.analyticsFilter == AnalyticsFilter.THIS_MONTH,
                            tag = "filter_this_month",
                            onClick = { onSelectFilter(AnalyticsFilter.THIS_MONTH) }
                        )
                        AnalyticsFilterChip(
                            label = strings.lastMonth,
                            selected = uiState.analyticsFilter == AnalyticsFilter.LAST_MONTH,
                            tag = "filter_last_month",
                            onClick = { onSelectFilter(AnalyticsFilter.LAST_MONTH) }
                        )
                        AnalyticsFilterChip(
                            label = if (uiState.analyticsFilter == AnalyticsFilter.CUSTOM_MONTH && uiState.customMonth != null) {
                                uiState.customMonth.label
                            } else {
                                strings.thisYear
                            },
                            selected = uiState.analyticsFilter == AnalyticsFilter.THIS_YEAR ||
                                uiState.analyticsFilter == AnalyticsFilter.CUSTOM_MONTH,
                            tag = "filter_this_year",
                            onClick = { onSelectFilter(AnalyticsFilter.THIS_YEAR) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Main White Rounded Chart & Breakdown Card matching Reference Image 2 (Right Phone)
                    val cardShape = RoundedCornerShape(30.dp)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = cardShape,
                                ambientColor = CardShadowTint,
                                spotColor = Color(0xFF9AB4E8)
                            ),
                        shape = cardShape,
                        color = CardWhite
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 3D Extruded Pie Chart
                            PieChart3D(
                                summaries = uiState.categorySummaries,
                                selectedCategory = selectedSliceCategory,
                                onSelectCategory = { selectedSliceCategory = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("analytics_pie_chart")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Expense Breakdown Rows: Food, Travel, Groceries, Others
                            uiState.categorySummaries.forEachIndexed { index, summary ->
                                val isHighlighted = selectedSliceCategory == summary.category
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isHighlighted) summary.category.primaryColor.copy(alpha = 0.08f)
                                            else Color.Transparent
                                        )
                                        .clickable {
                                            selectedSliceCategory =
                                                if (selectedSliceCategory == summary.category) null
                                                else summary.category
                                        }
                                        .padding(vertical = 10.dp, horizontal = 4.dp)
                                        .testTag("breakdown_row_${summary.category.name.lowercase()}"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 3D glossy spherical indicator dot
                                    Box(
                                        modifier = Modifier
                                            .size(21.dp)
                                            .shadow(
                                                elevation = 3.dp,
                                                shape = CircleShape,
                                                ambientColor = summary.category.primaryColor,
                                                spotColor = summary.category.primaryColor
                                            )
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        summary.category.gradientStart,
                                                        summary.category.gradientEnd,
                                                        summary.category.sideShadowColor
                                                    ),
                                                    center = Offset(14f, 12f),
                                                    radius = 36f
                                                ),
                                                shape = CircleShape
                                            )
                                            .border(
                                                width = 0.8.dp,
                                                color = Color.White.copy(alpha = 0.55f),
                                                shape = CircleShape
                                            )
                                    )

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Text(
                                        text = strings.categoryLabel(summary.category),
                                        color = PrimaryText,
                                        fontFamily = PoppinsFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(1.25f)
                                    )

                                    Text(
                                        text = CurrencyFormatter.formatRupees(summary.amount),
                                        color = PrimaryText,
                                        fontFamily = PoppinsFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.5.sp,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(0.95f)
                                    )

                                    Text(
                                        text = "${summary.percentage}%",
                                        color = SecondaryText,
                                        fontFamily = RobotoFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.5.sp,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(0.60f)
                                    )
                                }

                                if (index < uiState.categorySummaries.lastIndex) {
                                    HorizontalDivider(
                                        color = DividerSubtle,
                                        thickness = 1.dp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Download PDF Report Button
                            val btnShape = RoundedCornerShape(16.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .shadow(
                                        elevation = 10.dp,
                                        shape = btnShape,
                                        ambientColor = PrimaryBlue,
                                        spotColor = BrightBlue
                                    )
                                    .clip(btnShape)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF287EFF),
                                                PrimaryBlue,
                                                Color(0xFF1044D4)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.White.copy(alpha = 0.35f),
                                        shape = btnShape
                                    )
                                    .clickable(onClick = onDownloadPdfClick)
                                    .testTag("analytics_download_pdf_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.FileDownload,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(23.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strings.downloadPdfReport,
                                        color = Color.White,
                                        fontFamily = PoppinsFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        HisabBottomNavigationBar(
            selectedTab = MainNavTab.ANALYTICS,
            strings = strings,
            onSelectTab = onNavigateTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showCalendarDialog) {
        val recentMonths = remember {
            val sdf = SimpleDateFormat("MMM yyyy", Locale.US)
            (0..5).map { offset ->
                val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -offset) }
                Triple(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    sdf.format(cal.time)
                )
            }
        }
        AlertDialog(
            onDismissRequest = { showCalendarDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = "Select Month",
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    recentMonths.forEach { (year, month, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onSelectCustomMonth(year, month, label)
                                    showCalendarDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                color = PrimaryText,
                                fontFamily = RobotoFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCalendarDialog = false }) {
                    Text("Close", color = PrimaryBlue, fontFamily = PoppinsFamily)
                }
            }
        )
    }

    uiState.latestPdfResult?.let { result ->
        AlertDialog(
            onDismissRequest = onDismissPdfResult,
            containerColor = CardWhite,
            title = {
                Text(
                    text = "PDF Report Ready",
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (result.savedToDownloads) {
                        "Saved to Downloads/HisabKhata/${result.fileName}"
                    } else {
                        "Generated ${result.fileName}"
                    },
                    color = SecondaryText,
                    fontFamily = RobotoFamily,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(result.shareUri, "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        try {
                            context.startActivity(Intent.createChooser(viewIntent, "Open Expense Report"))
                        } catch (_: Exception) {
                        }
                        onDismissPdfResult()
                    }
                ) {
                    Text("Open PDF", color = PrimaryBlue, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, result.shareUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        try {
                            context.startActivity(Intent.createChooser(shareIntent, "Share Expense Report"))
                        } catch (_: Exception) {
                        }
                        onDismissPdfResult()
                    }
                ) {
                    Text("Share", color = SecondaryText, fontFamily = PoppinsFamily)
                }
            }
        )
    }
}

@Composable
private fun AnalyticsFilterChip(
    label: String,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    val chipShape = RoundedCornerShape(11.dp)
    Box(
        modifier = Modifier
            .clip(chipShape)
            .then(
                if (selected) {
                    Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF338BFF), PrimaryBlue)
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.35f),
                            shape = chipShape
                        )
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else FilterInactiveText,
            fontFamily = RobotoFamily,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 14.5.sp,
            maxLines = 1
        )
    }
}
