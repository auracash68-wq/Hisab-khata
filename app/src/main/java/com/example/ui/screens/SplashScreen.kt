package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AshokaChakraBadge
import com.example.ui.components.MicWaveformLogo
import com.example.ui.components.MonumentSilhouettes
import com.example.ui.components.TaglineWithLines
import com.example.ui.strings.LocalizedStrings
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BrandYellow
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.HeaderNavy
import com.example.ui.theme.IndianGreen
import com.example.ui.theme.PoppinsFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoFamily
import com.example.ui.theme.SaffronOrange
import com.example.ui.theme.SecondaryText
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SplashScreen(
    isDataReady: Boolean,
    strings: LocalizedStrings,
    onSplashComplete: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 0.78f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        progress.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(progress.value, isDataReady) {
        if (progress.value >= 0.99f && isDataReady) {
            delay(160)
            onSplashComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE7F0FF),
                        Color(0xFFFFFFFF),
                        AppBackground
                    )
                )
            )
    ) {
        // Upper curved navy/royal-blue backdrop + right-side tricolor curve + bottom tricolor wave
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Soft sky wash for middle monument zone
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFCEE1FF), Color(0xFFF6FAFF), Color.White),
                    startY = h * 0.30f,
                    endY = h * 0.65f
                ),
                topLeft = Offset(0f, h * 0.30f),
                size = androidx.compose.ui.geometry.Size(w, h * 0.35f)
            )

            // Main dark navy upper curved region
            val navyCurvePath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.31f)
                cubicTo(
                    w * 0.78f, h * 0.42f,
                    w * 0.25f, h * 0.48f,
                    0f, h * 0.37f
                )
                close()
            }
            drawPath(
                path = navyCurvePath,
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryNavy, HeaderNavy, PrimaryBlue),
                    startY = 0f,
                    endY = h * 0.47f
                )
            )

            // Subtle glossy blue highlight wave inside header
            val innerArcPath = Path().apply {
                moveTo(0f, h * 0.14f)
                cubicTo(w * 0.35f, h * 0.02f, w * 0.70f, h * 0.24f, w, h * 0.12f)
                lineTo(w, h * 0.31f)
                cubicTo(w * 0.78f, h * 0.42f, w * 0.25f, h * 0.48f, 0f, h * 0.37f)
                close()
            }
            drawPath(
                path = innerArcPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        BrightBlue.copy(alpha = 0.28f),
                        CyanGlow.copy(alpha = 0.10f)
                    )
                )
            )

            // Right-side Indian tricolor curve sweeping out of the navy curve
            val stripeW = h * 0.017f
            val rightTricolorColors = listOf(SaffronOrange, Color.White, IndianGreen)
            rightTricolorColors.forEachIndexed { idx, color ->
                val offset = (idx - 1) * stripeW * 0.98f
                val triPath = Path().apply {
                    moveTo(w * 0.48f, h * 0.38f + offset)
                    quadraticTo(w * 0.80f, h * 0.37f + offset, w + 16f, h * 0.20f + offset)
                }
                drawPath(
                    path = triPath,
                    color = color,
                    style = Stroke(width = stripeW, cap = StrokeCap.Round)
                )
            }

            // Re-draw left side of navy gloss cleanly where tricolor originates behind center
            val whiteGroundCurve = Path().apply {
                moveTo(0f, h * 0.62f)
                quadraticTo(w * 0.5f, h * 0.56f, w, h * 0.62f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = whiteGroundCurve,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF6F9FF)),
                    startY = h * 0.58f,
                    endY = h
                )
            )

            // Bottom Indian tricolor ribbon wave
            val bottomY = h * 0.905f
            val bStripe = h * 0.018f
            val bottomColors = listOf(SaffronOrange, Color.White, IndianGreen)
            bottomColors.forEachIndexed { idx, color ->
                val dy = (idx - 1) * bStripe * 0.98f
                val wavePath = Path().apply {
                    moveTo(-20f, bottomY + h * 0.025f + dy)
                    cubicTo(
                        w * 0.25f, bottomY - h * 0.035f + dy,
                        w * 0.68f, bottomY + h * 0.055f + dy,
                        w + 20f, bottomY - h * 0.020f + dy
                    )
                }
                drawPath(
                    path = wavePath,
                    color = color,
                    style = Stroke(width = bStripe, cap = StrokeCap.Round)
                )
            }
        }

        // Subtle Monument Silhouettes behind the central 3D illustration
        MonumentSilhouettes(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .align(Alignment.Center)
                .padding(bottom = 18.dp),
            silhouetteColor = Color(0xFF9DBCF2).copy(alpha = 0.34f)
        )

        // Foreground vertical layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            MicWaveformLogo(height = 76.dp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = strings.hisab,
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                lineHeight = 36.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = strings.khata,
                color = BrandYellow,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 38.sp,
                lineHeight = 38.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            TaglineWithLines(
                tagline = strings.tagline,
                fontSize = 14.5.sp
            )

            Spacer(modifier = Modifier.weight(0.45f))

            // Central 3D Illustration
            Image(
                painter = painterResource(id = R.drawable.img_splash_illustration),
                contentDescription = "Hisab Khata 3D wallet, rupee coins, calendar, clock, and receipt illustration",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(215.dp)
            )

            Spacer(modifier = Modifier.weight(0.35f))

            // Track • Analyze • Save row with subtle side lines
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFD3DEF5))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = strings.track,
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(6.dp)
                        .background(SaffronOrange, CircleShape)
                )
                Text(
                    text = strings.analyze,
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(6.dp)
                        .background(SaffronOrange, CircleShape)
                )
                Text(
                    text = strings.save,
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFD3DEF5))
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Rounded glossy loading progress bar with right-aligned percentage
            val pct = (progress.value * 100f).roundToInt().coerceIn(0, 100)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(28.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(14.dp),
                        ambientColor = Color(0xFF9DBAF2),
                        spotColor = Color(0xFF9DBAF2)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.2.dp, Color(0xFFD6E3FA), RoundedCornerShape(14.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.value.coerceAtLeast(0.06f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(CyanGlow, BrightBlue, PrimaryBlue)
                            )
                        )
                )
                Text(
                    text = "$pct%",
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = strings.loadingWait,
                color = SecondaryText,
                fontFamily = RobotoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.weight(0.35f))

            AshokaChakraBadge(size = 56.dp)

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
