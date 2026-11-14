package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.ExpenseCategory
import com.example.ui.strings.LocalizedStrings
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PoppinsFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoFamily
import com.example.ui.theme.SecondaryText
import com.example.util.VoiceExpenseParser
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceEntrySheet(
    strings: LocalizedStrings,
    onDismiss: () -> Unit,
    onSaveExpense: (ExpenseCategory, String, Int) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isListening by remember { mutableStateOf(false) }
    var rmsLevel by remember { mutableFloatStateOf(0f) }
    var spokenText by remember { mutableStateOf("") }
    var voiceStatus by remember { mutableStateOf("Tap mic and say e.g. \"250 Lunch at Restaurant\"") }

    var selectedCategory by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var amountText by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }

    val speechRecognizerAvailable = remember {
        SpeechRecognizer.isRecognitionAvailable(context)
    }

    val recognizer = remember {
        if (speechRecognizerAvailable) SpeechRecognizer.createSpeechRecognizer(context) else null
    }

    fun startListening() {
        if (recognizer == null) {
            voiceStatus = "Voice recognition unavailable on this device. Enter details below."
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("en", "IN").toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        isListening = true
        voiceStatus = "Listening..."
        recognizer.startListening(intent)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
        if (granted) {
            startListening()
        } else {
            voiceStatus = "Microphone permission denied. You can still type your expense below."
        }
    }

    DisposableEffect(recognizer) {
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                voiceStatus = "Listening... Speak now"
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {
                rmsLevel = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
                rmsLevel = 0f
            }

            override fun onError(error: Int) {
                isListening = false
                rmsLevel = 0f
                voiceStatus = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "Didn't catch that. Tap mic to retry or enter below."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard. Tap mic to retry."
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Enter details below."
                    else -> "Voice input ready. Tap mic or enter expense details below."
                }
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                rmsLevel = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val best = matches?.firstOrNull().orEmpty()
                if (best.isNotEmpty()) {
                    spokenText = best
                    val parsed = VoiceExpenseParser.parse(best)
                    selectedCategory = parsed.category
                    if (parsed.amount != null) {
                        amountText = parsed.amount.toString()
                    }
                    titleText = parsed.title
                    voiceStatus = "Recognized: \"$best\""
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()
                if (partial.isNotEmpty()) {
                    spokenText = partial
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        onDispose {
            recognizer?.destroy()
        }
    }

    LaunchedEffect(Unit) {
        if (hasAudioPermission && speechRecognizerAvailable) {
            startListening()
        } else if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            voiceStatus = "Voice service unavailable. Enter your expense below."
        }
    }

    val micPulseScale by animateFloatAsState(
        targetValue = if (isListening) 1f + rmsLevel * 0.22f else 1f,
        label = "micPulse"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Voice Expense Entry",
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interactive Microphone Trigger
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .scale(micPulseScale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (isListening) DangerRed.copy(alpha = 0.25f) else CyanGlow.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (isListening) {
                                    listOf(DangerRed, Color(0xFFC41414))
                                } else {
                                    listOf(BrightBlue, PrimaryBlue)
                                }
                            )
                        )
                        .border(2.dp, Color.White, CircleShape)
                        .clickable {
                            if (isListening) {
                                recognizer?.stopListening()
                                isListening = false
                            } else if (hasAudioPermission) {
                                startListening()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                        .testTag("voice_sheet_mic_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Rounded.Stop else Icons.Rounded.Mic,
                        contentDescription = if (isListening) "Stop listening" else "Start listening",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = voiceStatus,
                color = SecondaryText,
                fontFamily = RobotoFamily,
                fontSize = 13.sp
            )

            if (spokenText.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"$spokenText\"",
                    color = PrimaryBlue,
                    fontFamily = RobotoFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExpenseCategory.entries.forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = strings.categoryLabel(category),
                                fontFamily = RobotoFamily,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = category.primaryColor,
                            selectedLabelColor = Color.White,
                            containerColor = AppBackground,
                            labelColor = PrimaryText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    amountText = input.filter { it.isDigit() }.take(7)
                },
                label = { Text("Amount (₹)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_amount_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Description") },
                placeholder = { Text("e.g. Lunch at Restaurant") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_title_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            val parsedAmount = amountText.toIntOrNull()
            Button(
                onClick = {
                    if (parsedAmount != null && parsedAmount > 0) {
                        onSaveExpense(
                            selectedCategory,
                            titleText.ifBlank { strings.categoryLabel(selectedCategory) },
                            parsedAmount
                        )
                    }
                },
                enabled = parsedAmount != null && parsedAmount > 0,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("voice_save_button")
            ) {
                Icon(imageVector = Icons.Rounded.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Expense",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
