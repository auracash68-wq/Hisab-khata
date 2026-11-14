package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import com.example.ui.HisabKhataUiState
import com.example.ui.components.ExpenseItemCard
import com.example.ui.components.FloatingMicButton
import com.example.ui.components.HisabBottomNavigationBar
import com.example.ui.components.HisabKhataInlineBrand
import com.example.ui.components.MainNavTab
import com.example.ui.components.MonumentSilhouettes
import com.example.ui.components.categoryLabel
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CardShadowTint
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DangerRed
import com.example.ui.theme.HeaderNavy
import com.example.ui.theme.PoppinsFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoFamily
import com.example.ui.theme.SecondaryText
import com.example.util.CurrencyFormatter
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    uiState: HisabKhataUiState,
    onNavigateTab: (MainNavTab) -> Unit,
    onViewAllClick: () -> Unit,
    onMicClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onDeleteExpense: (Long) -> Unit
) {
    val strings = uiState.strings
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var selectedExpenseForAction by remember { mutableStateOf<ExpenseEntity?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CardWhite
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(listOf(PrimaryNavy, HeaderNavy))
                        )
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 20.dp, vertical = 22.dp)
                ) {
                    Column {
                        HisabKhataInlineBrand(fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.tagline,
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = RobotoFamily,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                    label = { Text(strings.navHome, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PrimaryBlue.copy(alpha = 0.1f),
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.ReceiptLong, contentDescription = null) },
                    label = { Text(strings.allExpensesTitle, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onViewAllClick()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.BarChart, contentDescription = null) },
                    label = { Text(strings.navAnalytics, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateTab(MainNavTab.ANALYTICS)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                    label = { Text(strings.navSettings, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateTab(MainNavTab.SETTINGS)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppBackground,
                            Color(0xFFF3F7FF),
                            Color(0xFFDCE8FC)
                        )
                    )
                )
        ) {
            // Subtle lower monument silhouette backdrop behind mic & bottom area
            MonumentSilhouettes(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 58.dp),
                silhouetteColor = Color(0xFF9AB8EC).copy(alpha = 0.34f)
            )

            // Main vertical scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dark Navy upper header extending behind the Total Spent card
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(215.dp)
                    ) {
                        val w = size.width
                        val h = size.height
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(w, 0f)
                            lineTo(w, h * 0.72f)
                            quadraticTo(w * 0.5f, h * 0.86f, 0f, h * 0.72f)
                            close()
                        }
                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(PrimaryNavy, HeaderNavy, Color(0xFF0E2A6E))
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
                        // Top App Bar: Hamburger | HISAB KHATA | Bell with red dot
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("home_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Menu,
                                    contentDescription = stringResource(R.string.cd_menu),
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            HisabKhataInlineBrand(fontSize = 20.5.sp)

                            IconButton(
                                onClick = {
                                    onNotificationsClick()
                                    showNotificationsDialog = true
                                },
                                modifier = Modifier.testTag("home_notifications_button")
                            ) {
                                Box {
                                    Icon(
                                        imageVector = Icons.Outlined.Notifications,
                                        contentDescription = stringResource(R.string.cd_notifications),
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    if (uiState.hasUnreadAlert) {
                                        Box(
                                            modifier = Modifier
                                                .size(9.dp)
                                                .align(Alignment.TopEnd)
                                                .offset(x = (-1).dp, y = 1.dp)
                                                .background(DangerRed, CircleShape)
                                                .border(1.2.dp, HeaderNavy, CircleShape)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Total Spent Hero Card with bright blue lower decorative border & sunburst rays
                        TotalSpentCard(
                            label = strings.totalSpent,
                            formattedTotal = CurrencyFormatter.formatRupees(uiState.totalSpentThisMonth),
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    }
                }

                // Body section: Today's Expenses + View All > + 3 Expense Cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.todaysExpenses,
                            color = PrimaryText,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.5.sp
                        )

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onViewAllClick)
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("home_view_all_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.viewAll,
                                color = PrimaryBlue,
                                fontFamily = RobotoFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.todaysTopThree.isEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(18.dp),
                                    ambientColor = CardShadowTint
                                ),
                            shape = RoundedCornerShape(18.dp),
                            color = CardWhite
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp, horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = strings.noExpensesYet,
                                    color = PrimaryText,
                                    fontFamily = PoppinsFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = strings.tapMicHint,
                                    color = SecondaryText,
                                    fontFamily = RobotoFamily,
                                    fontSize = 13.5.sp
                                )
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            uiState.todaysTopThree.forEach { expense ->
                                ExpenseItemCard(
                                    expense = expense,
                                    strings = strings,
                                    onClick = { selectedExpenseForAction = expense }
                                )
                            }
                        }
                    }

                    // Bottom clearance for floating mic + bottom nav bar
                    Spacer(modifier = Modifier.height(145.dp))
                }
            }

            // Bottom Navigation Bar
            HisabBottomNavigationBar(
                selectedTab = MainNavTab.HOME,
                strings = strings,
                onSelectTab = onNavigateTab,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            // Large Illuminated Floating Microphone Button positioned above the bottom navigation bar
            FloatingMicButton(
                onClick = onMicClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 84.dp)
            )
        }
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = "Daily Summary",
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Today you spent ${CurrencyFormatter.formatRupees(uiState.todayTotal)} across ${uiState.todaysTopThree.size} recent entries. This month's total is ${CurrencyFormatter.formatRupees(uiState.totalSpentThisMonth)}.",
                    color = SecondaryText,
                    fontFamily = RobotoFamily,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("OK", color = PrimaryBlue, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
                }
            }
        )
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
                    fontFamily = RobotoFamily,
                    fontSize = 14.sp
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
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        tint = DangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", color = DangerRed, fontFamily = PoppinsFamily)
                }
            }
        )
    }
}

@Composable
private fun TotalSpentCard(
    label: String,
    formattedTotal: String,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(24.dp)
    // Outer container creates the bright blue glowing lower decorative border seen in the reference
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(144.dp)
            .shadow(
                elevation = 14.dp,
                shape = cardShape,
                ambientColor = BrightBlue.copy(alpha = 0.55f),
                spotColor = PrimaryBlue.copy(alpha = 0.65f)
            )
            .clip(cardShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFA7CEFF),
                        BrightBlue,
                        PrimaryBlue
                    )
                )
            )
            .padding(start = 1.5.dp, end = 1.5.dp, top = 1.2.dp, bottom = 4.5.dp)
    ) {
        // Inner sky-blue to ice-white gradient surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(22.5.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFC4DFFF),
                            Color(0xFFEAF4FF),
                            Color(0xFFF7FBFF),
                            Color(0xFFCBE6FF)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 420f)
                    )
                )
        ) {
            // Subtle sunburst rays, soft financial wave, and cyan highlights matching Reference Image 2
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val rayOrigin = Offset(w * 0.58f, h * 0.18f)

                // Sunburst light beams
                for (i in 0 until 9) {
                    val angleDeg = 15f + i * 19f
                    val r1 = (angleDeg - 4.5f) * PI / 180.0
                    val r2 = (angleDeg + 4.5f) * PI / 180.0
                    val len = w * 1.1f
                    val rayPath = Path().apply {
                        moveTo(rayOrigin.x, rayOrigin.y)
                        lineTo(
                            rayOrigin.x + (len * cos(r1)).toFloat(),
                            rayOrigin.y + (len * sin(r1)).toFloat()
                        )
                        lineTo(
                            rayOrigin.x + (len * cos(r2)).toFloat(),
                            rayOrigin.y + (len * sin(r2)).toFloat()
                        )
                        close()
                    }
                    drawPath(
                        path = rayPath,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.48f),
                                Color.Transparent
                            ),
                            center = rayOrigin,
                            radius = w * 0.78f
                        )
                    )
                }

                // Soft cyan-blue wave along the bottom-left
                val wavePath = Path().apply {
                    moveTo(0f, h * 0.72f)
                    cubicTo(
                        w * 0.25f, h * 0.96f,
                        w * 0.52f, h * 0.68f,
                        w * 0.85f, h
                    )
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = wavePath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF9AC8FF).copy(alpha = 0.42f),
                            CyanGlow.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                )

                // Subtle glowing cyan highlight on the bottom-right under the wallet
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CyanGlow.copy(alpha = 0.32f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.72f, h * 0.88f),
                        radius = w * 0.32f
                    ),
                    radius = w * 0.32f,
                    center = Offset(w * 0.72f, h * 0.88f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 6.dp, top = 8.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.05f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = label,
                        color = PrimaryNavy,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.5.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formattedTotal,
                        color = PrimaryNavy,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 38.sp,
                        lineHeight = 42.sp,
                        maxLines = 1,
                        modifier = Modifier.testTag("home_total_spent_text")
                    )
                }

                // Prominent 3D blue wallet + golden rupee coins + cyan financial growth chart
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_wallet_illustration),
                        contentDescription = "3D wallet with rupee coins and upward spending chart",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxHeight()
                            .scale(1.12f)
                            .offset(x = (-2).dp, y = 2.dp)
                    )
                }
            }
        }
    }
}
