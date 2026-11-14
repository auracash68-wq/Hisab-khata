package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.HisabKhataUiState
import com.example.ui.components.HisabKhataInlineBrand
import com.example.ui.components.MicWaveformLogo
import com.example.ui.components.TaglineWithLines
import com.example.ui.strings.AppLanguage
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CardWhite
import com.example.ui.theme.DangerRed
import com.example.ui.theme.HeaderNavy
import com.example.ui.theme.IndianGreen
import com.example.ui.theme.PoppinsFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoFamily
import com.example.ui.theme.SaffronOrange
import com.example.ui.theme.SecondaryText
import com.example.util.CurrencyFormatter

@Composable
fun SettingsScreen(
    uiState: HisabKhataUiState,
    onBackClick: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onExportBackup: (android.net.Uri) -> Unit,
    onRestoreBackup: (android.net.Uri) -> Unit,
    onConfirmClearAllData: () -> Unit
) {
    BackHandler(onBack = onBackClick)

    val strings = uiState.strings
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            onExportBackup(uri)
            showBackupDialog = false
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onRestoreBackup(uri)
            showBackupDialog = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF3F7FE), Color(0xFFFFFFFF))
                )
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upper Curved Dark Navy Branding Section with Left Blue Sweep & Right Indian Tricolor Ribbons
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(312.dp)
            ) {
                val w = size.width
                val h = size.height

                // 1. Bottom-right Green curved ribbon (lowest band of tricolor)
                val greenBand = Path().apply {
                    moveTo(w * 0.46f, h * 0.965f)
                    quadraticTo(w * 0.78f, h * 0.955f, w, h * 0.77f)
                    lineTo(w, h * 0.67f)
                    quadraticTo(w * 0.76f, h * 0.90f, w * 0.46f, h * 0.965f)
                    close()
                }
                drawPath(path = greenBand, color = IndianGreen)

                // 2. Middle White curved ribbon
                val whiteBand = Path().apply {
                    moveTo(w * 0.48f, h * 0.962f)
                    quadraticTo(w * 0.77f, h * 0.905f, w, h * 0.675f)
                    lineTo(w, h * 0.575f)
                    quadraticTo(w * 0.75f, h * 0.845f, w * 0.48f, h * 0.962f)
                    close()
                }
                drawPath(path = whiteBand, color = Color.White)

                // 3. Top Saffron Orange curved ribbon
                val saffronBand = Path().apply {
                    moveTo(w * 0.50f, h * 0.958f)
                    quadraticTo(w * 0.76f, h * 0.85f, w, h * 0.58f)
                    lineTo(w, h * 0.475f)
                    quadraticTo(w * 0.74f, h * 0.785f, w * 0.50f, h * 0.958f)
                    close()
                }
                drawPath(path = saffronBand, color = SaffronOrange)

                // 4. Bottom-left Royal Blue curved accent ribbon (as seen in Reference Image 1)
                val leftBlueBand = Path().apply {
                    moveTo(0f, h * 0.70f)
                    quadraticTo(w * 0.22f, h * 0.91f, w * 0.48f, h * 0.962f)
                    quadraticTo(w * 0.20f, h * 0.945f, 0f, h * 0.79f)
                    close()
                }
                drawPath(
                    path = leftBlueBand,
                    brush = Brush.horizontalGradient(
                        colors = listOf(PrimaryBlue, Color(0xFF0A39B5))
                    )
                )

                // 5. Main Deep Navy-Blue Upper Branding Area
                val navyPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(w, h * 0.485f)
                    cubicTo(
                        w * 0.76f, h * 0.81f,
                        w * 0.42f, h * 1.02f,
                        0f, h * 0.71f
                    )
                    close()
                }
                drawPath(
                    path = navyPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF05163B),
                            PrimaryNavy,
                            HeaderNavy,
                            Color(0xFF0D2B6E)
                        )
                    )
                )

                // 6. Subtle diagonal curved glossy wave streaks inside the navy header
                val innerWave1 = Path().apply {
                    moveTo(0f, h * 0.34f)
                    cubicTo(w * 0.35f, h * 0.14f, w * 0.68f, h * 0.38f, w, h * 0.12f)
                    lineTo(w, h * 0.485f)
                    cubicTo(w * 0.76f, h * 0.81f, w * 0.42f, h * 1.02f, 0f, h * 0.71f)
                    close()
                }
                drawPath(
                    path = innerWave1,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            BrightBlue.copy(alpha = 0.13f),
                            PrimaryBlue.copy(alpha = 0.08f)
                        ),
                        start = Offset(0f, h * 0.2f),
                        end = Offset(w, h * 0.8f)
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.statusBars),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar: Back Arrow + Centered Settings Title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Text(
                        text = strings.settingsTitle,
                        color = Color.White,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Prominent Microphone & Waveform Logo matching Reference Image 1
                MicWaveformLogo(height = 76.dp)

                Spacer(modifier = Modifier.height(6.dp))

                // Large HISAB KHATA Branding
                HisabKhataInlineBrand(fontSize = 27.sp)

                Spacer(modifier = Modifier.height(4.dp))

                // Smart Expense Tracker with horizontal lines
                TaglineWithLines(
                    tagline = strings.tagline,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Exactly Three Elevated White Rounded Settings Cards matching Reference Image 1
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsOptionCard(
                icon = Icons.Rounded.Public,
                outerRingColor = Color(0xFF4CA8FF),
                iconGradient = listOf(Color(0xFF2E95FF), PrimaryBlue, Color(0xFF0A42CC)),
                shadowColor = PrimaryBlue,
                title = strings.languageTitle,
                description = strings.languageDesc,
                tag = "settings_language_card",
                onClick = { showLanguageDialog = true }
            )

            SettingsOptionCard(
                icon = Icons.Rounded.CloudSync,
                outerRingColor = Color(0xFF4CA8FF),
                iconGradient = listOf(Color(0xFF2E95FF), PrimaryBlue, Color(0xFF0A42CC)),
                shadowColor = PrimaryBlue,
                title = strings.dataBackupTitle,
                description = strings.dataBackupDesc,
                tag = "settings_backup_card",
                onClick = { showBackupDialog = true }
            )

            SettingsOptionCard(
                icon = Icons.Rounded.Delete,
                outerRingColor = Color(0xFFFF6E6E),
                iconGradient = listOf(Color(0xFFFF4D4D), DangerRed, Color(0xFFC21111)),
                shadowColor = DangerRed,
                title = strings.clearAllDataTitle,
                description = strings.clearAllDataDesc,
                tag = "settings_clear_card",
                onClick = { showClearConfirmDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = strings.languageTitle,
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppLanguage.entries.forEach { lang ->
                        val isSelected = uiState.language == lang
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) PrimaryBlue.copy(alpha = 0.1f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    onSelectLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                                .testTag("language_option_${lang.code}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = lang.nativeName,
                                    color = if (isSelected) PrimaryBlue else PrimaryText,
                                    fontFamily = PoppinsFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                if (lang.displayName != lang.nativeName) {
                                    Text(
                                        text = lang.displayName,
                                        color = SecondaryText,
                                        fontFamily = RobotoFamily,
                                        fontSize = 12.5.sp
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Selected",
                                    tint = PrimaryBlue
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = PrimaryBlue, fontFamily = PoppinsFamily)
                }
            }
        )
    }

    // Data Backup Dialog (Real SAF Export / Restore JSON)
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = strings.dataBackupTitle,
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val lastBackupText = uiState.lastBackupMillis?.let {
                        "Last backup: ${CurrencyFormatter.formatDateTime(it)}"
                    } ?: "No external backup created yet."
                    Text(
                        text = "${uiState.allExpenses.size} expenses stored locally.\n$lastBackupText",
                        color = SecondaryText,
                        fontFamily = RobotoFamily,
                        fontSize = 14.sp
                    )
                    Button(
                        onClick = {
                            exportLauncher.launch("HisabKhata_Backup_${System.currentTimeMillis()}.json")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("backup_export_button")
                    ) {
                        Text("Export Backup (.json)", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = {
                            restoreLauncher.launch(arrayOf("application/json", "text/plain"))
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("backup_restore_button")
                    ) {
                        Text("Restore from Backup", color = PrimaryBlue, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Close", color = SecondaryText, fontFamily = PoppinsFamily)
                }
            }
        )
    }

    // Clear All Data Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = strings.clearAllDataTitle,
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete all saved expenses? This action cannot be undone.",
                    color = SecondaryText,
                    fontFamily = RobotoFamily,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmClearAllData()
                        showClearConfirmDialog = false
                    },
                    modifier = Modifier.testTag("confirm_clear_data_button")
                ) {
                    Text("Delete All", color = DangerRed, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = PrimaryBlue, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

@Composable
private fun SettingsOptionCard(
    icon: ImageVector,
    outerRingColor: Color,
    iconGradient: List<Color>,
    shadowColor: Color,
    title: String,
    description: String,
    tag: String,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(22.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = cardShape,
                ambientColor = Color(0xFF9EB6E8),
                spotColor = Color(0xFFA5BFEF)
            )
            .clip(cardShape)
            .border(
                width = 1.dp,
                color = Color(0xFFE5EEFC),
                shape = cardShape
            )
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = cardShape,
        color = CardWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large 3D glossy circular icon badge with outer bevel ring matching Reference Image 1
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        ambientColor = shadowColor,
                        spotColor = shadowColor
                    )
                    .background(
                        brush = Brush.verticalGradient(colors = iconGradient),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.5.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                outerRingColor,
                                iconGradient.last()
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    color = SecondaryText,
                    fontFamily = RobotoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = stringResource(R.string.cd_chevron),
                tint = Color(0xFF2D4373),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
