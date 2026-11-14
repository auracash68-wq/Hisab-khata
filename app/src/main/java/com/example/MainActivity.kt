package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.HisabKhataViewModel
import com.example.ui.components.MainNavTab
import com.example.ui.components.VoiceEntrySheet
import com.example.ui.screens.AllExpensesScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.HisabKhataTheme

const val ROUTE_SPLASH = "splash"
const val ROUTE_HOME = "home"
const val ROUTE_ANALYTICS = "analytics"
const val ROUTE_SETTINGS = "settings"
const val ROUTE_ALL_EXPENSES = "all_expenses"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(
                AndroidColor.TRANSPARENT,
                AndroidColor.TRANSPARENT
            )
        )
        setContent {
            HisabKhataTheme {
                HisabKhataApp()
            }
        }
    }
}

@Composable
fun HisabKhataApp(
    viewModel: HisabKhataViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showVoiceSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.statusMessage) {
        val msg = uiState.statusMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    fun navigateToTab(tab: MainNavTab) {
        val destination = when (tab) {
            MainNavTab.HOME -> ROUTE_HOME
            MainNavTab.ANALYTICS -> ROUTE_ANALYTICS
            MainNavTab.SETTINGS -> ROUTE_SETTINGS
        }
        navController.navigate(destination) {
            popUpTo(ROUTE_HOME) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = ROUTE_SPLASH
        ) {
            composable(ROUTE_SPLASH) {
                SplashScreen(
                    isDataReady = uiState.isReady,
                    strings = uiState.strings,
                    onSplashComplete = {
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(ROUTE_HOME) {
                HomeScreen(
                    uiState = uiState,
                    onNavigateTab = ::navigateToTab,
                    onViewAllClick = { navController.navigate(ROUTE_ALL_EXPENSES) },
                    onMicClick = { showVoiceSheet = true },
                    onNotificationsClick = { viewModel.markNotificationsRead() },
                    onDeleteExpense = { id -> viewModel.deleteExpense(id) }
                )
            }

            composable(ROUTE_ANALYTICS) {
                AnalyticsScreen(
                    uiState = uiState,
                    onBackClick = {
                        if (!navController.popBackStack()) {
                            navigateToTab(MainNavTab.HOME)
                        }
                    },
                    onNavigateTab = ::navigateToTab,
                    onSelectFilter = { viewModel.selectAnalyticsFilter(it) },
                    onSelectCustomMonth = { y, m, label ->
                        viewModel.selectCustomMonth(y, m, label)
                    },
                    onDownloadPdfClick = { viewModel.exportPdfReport(context) },
                    onDismissPdfResult = { viewModel.clearLatestPdfResult() }
                )
            }

            composable(ROUTE_SETTINGS) {
                SettingsScreen(
                    uiState = uiState,
                    onBackClick = {
                        if (!navController.popBackStack()) {
                            navigateToTab(MainNavTab.HOME)
                        }
                    },
                    onSelectLanguage = { viewModel.setLanguage(it) },
                    onExportBackup = { uri -> viewModel.exportBackupToUri(context, uri) },
                    onRestoreBackup = { uri -> viewModel.restoreBackupFromUri(context, uri) },
                    onConfirmClearAllData = { viewModel.clearAllData() }
                )
            }

            composable(ROUTE_ALL_EXPENSES) {
                AllExpensesScreen(
                    uiState = uiState,
                    onBackClick = { navController.popBackStack() },
                    onDeleteExpense = { id -> viewModel.deleteExpense(id) }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 80.dp)
        )
    }

    if (showVoiceSheet) {
        VoiceEntrySheet(
            strings = uiState.strings,
            onDismiss = { showVoiceSheet = false },
            onSaveExpense = { category, title, amount ->
                viewModel.addExpense(category, title, amount)
                showVoiceSheet = false
            }
        )
    }
}
