package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandYellow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.IndianGreen
import com.example.ui.theme.PoppinsFamily
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.RobotoFamily
import com.example.ui.theme.SaffronOrange
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MicWaveformLogo(
    modifier: Modifier = Modifier,
    height: Dp = 84.dp
) {
    Canvas(
        modifier = modifier
            .width(height * 2.45f)
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h * 0.50f

        // Soft glowing halo behind the microphone capsule
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(CyanGlow.copy(alpha = 0.22f), Color.Transparent),
                center = Offset(cx, cy * 0.88f),
                radius = h * 0.68f
            ),
            radius = h * 0.68f,
            center = Offset(cx, cy * 0.88f)
        )

        val barWidth = h * 0.088f
        val barCorner = CornerRadius(barWidth / 2f, barWidth / 2f)
        val startOffset = h * 0.36f
        val step = h * 0.155f

        // Left waveform bars (inner to outer: green -> bright lime -> yellow) matching Reference Image 1
        val leftHeights = listOf(0.28f, 0.50f, 0.68f, 0.42f, 0.20f)
        val leftTopColors = listOf(
            Color(0xFF1EE000),
            Color(0xFF42F500),
            Color(0xFF88FF00),
            Color(0xFFC8FF00),
            Color(0xFFFFE600)
        )
        val leftBottomColors = listOf(
            Color(0xFF0E9E00),
            Color(0xFF16C900),
            Color(0xFF38D900),
            Color(0xFF8CE000),
            Color(0xFFFFB700)
        )
        leftHeights.forEachIndexed { i, ratio ->
            val bh = (h * ratio).coerceAtLeast(barWidth)
            val x = cx - startOffset - i * step - barWidth / 2f
            val y = cy - bh / 2f + h * 0.04f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(leftTopColors[i], leftBottomColors[i]),
                    startY = y,
                    endY = y + bh
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, bh),
                cornerRadius = barCorner
            )
        }

        // Right waveform bars (inner to outer: deep orange -> saffron -> golden yellow)
        val rightHeights = listOf(0.28f, 0.50f, 0.68f, 0.42f, 0.20f)
        val rightTopColors = listOf(
            Color(0xFFFF8800),
            Color(0xFFFF9E00),
            Color(0xFFFFB300),
            Color(0xFFFFC400),
            Color(0xFFFFD900)
        )
        val rightBottomColors = listOf(
            Color(0xFFE65100),
            Color(0xFFFF6D00),
            Color(0xFFFF7B00),
            Color(0xFFFF8F00),
            Color(0xFFFFA000)
        )
        rightHeights.forEachIndexed { i, ratio ->
            val bh = (h * ratio).coerceAtLeast(barWidth)
            val x = cx + startOffset + i * step - barWidth / 2f
            val y = cy - bh / 2f + h * 0.04f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(rightTopColors[i], rightBottomColors[i]),
                    startY = y,
                    endY = y + bh
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, bh),
                cornerRadius = barCorner
            )
        }

        // Center white microphone capsule
        val micW = h * 0.30f
        val micH = h * 0.50f
        val micTop = h * 0.06f
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.White, Color(0xFFEEF5FF), Color.White),
                startX = cx - micW / 2f,
                endX = cx + micW / 2f
            ),
            topLeft = Offset(cx - micW / 2f, micTop),
            size = Size(micW, micH),
            cornerRadius = CornerRadius(micW / 2f, micW / 2f)
        )

        // U-shaped cradle around microphone
        val cradleStroke = h * 0.072f
        val cradleW = h * 0.47f
        val cradleH = h * 0.43f
        val cradleTop = h * 0.28f
        drawArc(
            color = Color.White,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - cradleW / 2f, cradleTop),
            size = Size(cradleW, cradleH),
            style = Stroke(width = cradleStroke, cap = StrokeCap.Round)
        )

        // Vertical stem and horizontal base
        val stemTop = cradleTop + cradleH
        val stemBottom = h * 0.89f
        drawLine(
            color = Color.White,
            start = Offset(cx, stemTop),
            end = Offset(cx, stemBottom),
            strokeWidth = cradleStroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(cx - h * 0.17f, stemBottom),
            end = Offset(cx + h * 0.17f, stemBottom),
            strokeWidth = cradleStroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun HisabKhataInlineBrand(
    fontSize: TextUnit = 21.sp,
    modifier: Modifier = Modifier
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = Color.White,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.ExtraBold
                )
            ) {
                append("HISAB ")
            }
            withStyle(
                SpanStyle(
                    color = BrandYellow,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.ExtraBold
                )
            ) {
                append("KHATA")
            }
        },
        fontSize = fontSize,
        letterSpacing = 0.5.sp,
        modifier = modifier
    )
}

@Composable
fun TaglineWithLines(
    tagline: String,
    lineColor: Color = Color.White.copy(alpha = 0.72f),
    textColor: Color = Color.White,
    fontSize: TextUnit = 14.sp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(0.86f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.2.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawLine(
                    color = lineColor,
                    start = Offset(size.width * 0.15f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = size.height
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = tagline,
            color = textColor,
            fontFamily = RobotoFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSize,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.2.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawLine(
                    color = lineColor,
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width * 0.85f, size.height / 2f),
                    strokeWidth = size.height
                )
            }
        }
    }
}

@Composable
fun AshokaChakraBadge(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val r = this.size.minDimension / 2f
        val center = Offset(r, r)

        drawCircle(
            color = Color.White,
            radius = r,
            center = center
        )

        val navy = PrimaryNavy
        val outerRingR = r * 0.80f
        val innerHubR = r * 0.14f

        drawCircle(
            color = navy,
            radius = outerRingR,
            center = center,
            style = Stroke(width = r * 0.08f)
        )
        drawCircle(
            color = navy,
            radius = innerHubR,
            center = center
        )

        val spokeStroke = r * 0.036f
        for (i in 0 until 24) {
            val angleRad = (i * 15.0) * PI / 180.0
            val sx = center.x + (innerHubR * 0.9f) * cos(angleRad).toFloat()
            val sy = center.y + (innerHubR * 0.9f) * sin(angleRad).toFloat()
            val ex = center.x + (outerRingR * 0.96f) * cos(angleRad).toFloat()
            val ey = center.y + (outerRingR * 0.96f) * sin(angleRad).toFloat()
            drawLine(
                color = navy,
                start = Offset(sx, sy),
                end = Offset(ex, ey),
                strokeWidth = spokeStroke
            )

            val midAngle = ((i * 15.0) + 7.5) * PI / 180.0
            val dotR = outerRingR * 0.92f
            drawCircle(
                color = navy,
                radius = r * 0.025f,
                center = Offset(
                    center.x + dotR * cos(midAngle).toFloat(),
                    center.y + dotR * sin(midAngle).toFloat()
                )
            )
        }
    }
}

@Composable
fun MonumentSilhouettes(
    modifier: Modifier = Modifier,
    silhouetteColor: Color = Color(0xFF9DBCF2).copy(alpha = 0.28f)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Left: Taj Mahal silhouette
        val tajBaseY = h
        val tajCenterX = w * 0.18f
        val tajW = w * 0.34f

        drawRect(
            color = silhouetteColor,
            topLeft = Offset(0f, tajBaseY - h * 0.10f),
            size = Size(tajW * 1.15f, h * 0.10f)
        )
        drawRect(
            color = silhouetteColor,
            topLeft = Offset(tajCenterX - tajW * 0.32f, tajBaseY - h * 0.42f),
            size = Size(tajW * 0.64f, h * 0.32f)
        )
        val domePath = Path().apply {
            val dw = tajW * 0.30f
            val dh = h * 0.32f
            val baseD = tajBaseY - h * 0.42f
            moveTo(tajCenterX - dw / 2f, baseD)
            cubicTo(
                tajCenterX - dw * 0.62f, baseD - dh * 0.55f,
                tajCenterX - dw * 0.18f, baseD - dh * 0.85f,
                tajCenterX, baseD - dh
            )
            cubicTo(
                tajCenterX + dw * 0.18f, baseD - dh * 0.85f,
                tajCenterX + dw * 0.62f, baseD - dh * 0.55f,
                tajCenterX + dw / 2f, baseD
            )
            close()
        }
        drawPath(domePath, silhouetteColor)
        drawLine(
            color = silhouetteColor,
            start = Offset(tajCenterX, tajBaseY - h * 0.74f),
            end = Offset(tajCenterX, tajBaseY - h * 0.84f),
            strokeWidth = 3f
        )
        val minaretOffsets = listOf(-0.42f, -0.32f, 0.32f, 0.42f)
        minaretOffsets.forEach { offsetFactor ->
            val mx = tajCenterX + tajW * offsetFactor
            val mw = tajW * 0.045f
            val mh = if (kotlin.math.abs(offsetFactor) > 0.38f) h * 0.48f else h * 0.36f
            drawRect(
                color = silhouetteColor,
                topLeft = Offset(mx - mw / 2f, tajBaseY - mh),
                size = Size(mw, mh)
            )
            drawCircle(
                color = silhouetteColor,
                radius = mw * 0.7f,
                center = Offset(mx, tajBaseY - mh)
            )
        }

        // Right: Mosque / Monument silhouette
        val igCenterX = w * 0.84f
        val igW = w * 0.28f
        val igH = h * 0.68f
        val igTop = h - igH
        val gatePath = Path().apply {
            moveTo(igCenterX - igW * 0.48f, h)
            lineTo(igCenterX - igW * 0.48f, igTop + igH * 0.22f)
            lineTo(igCenterX - igW * 0.38f, igTop + igH * 0.14f)
            lineTo(igCenterX - igW * 0.38f, igTop + igH * 0.06f)
            lineTo(igCenterX + igW * 0.38f, igTop + igH * 0.06f)
            lineTo(igCenterX + igW * 0.38f, igTop + igH * 0.14f)
            lineTo(igCenterX + igW * 0.48f, igTop + igH * 0.22f)
            lineTo(igCenterX + igW * 0.48f, h)
            lineTo(igCenterX + igW * 0.19f, h)
            lineTo(igCenterX + igW * 0.19f, igTop + igH * 0.56f)
            arcTo(
                rect = Rect(
                    left = igCenterX - igW * 0.19f,
                    top = igTop + igH * 0.36f,
                    right = igCenterX + igW * 0.19f,
                    bottom = igTop + igH * 0.76f
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            lineTo(igCenterX - igW * 0.19f, h)
            close()
        }
        drawPath(gatePath, silhouetteColor)
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTricolorCurve(
    start: Offset,
    control: Offset,
    end: Offset,
    stripeThickness: Float
) {
    val colors = listOf(SaffronOrange, Color.White, IndianGreen)
    colors.forEachIndexed { index, color ->
        val dy = (index - 1) * stripeThickness * 0.96f
        val path = Path().apply {
            moveTo(start.x, start.y + dy)
            quadraticTo(control.x, control.y + dy, end.x, end.y + dy)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = stripeThickness, cap = StrokeCap.Round)
        )
    }
}
