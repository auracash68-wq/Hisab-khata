package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HisabKhataColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryNavy,
    onPrimaryContainer = Color.White,
    secondary = BrightBlue,
    onSecondary = Color.White,
    tertiary = BrandYellow,
    onTertiary = PrimaryText,
    background = AppBackground,
    onBackground = PrimaryText,
    surface = CardWhite,
    onSurface = PrimaryText,
    surfaceVariant = SoftBlueCardStart,
    onSurfaceVariant = SecondaryText,
    error = DangerRed,
    onError = Color.White,
    outline = SoftBlueBorder
)

@Composable
fun HisabKhataTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HisabKhataColorScheme,
        typography = Typography,
        content = content
    )
}
