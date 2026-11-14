package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.ExpenseCategory
import com.example.util.CategorySummary
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private data class SliceAngles(
    val summary: CategorySummary,
    val startAngle: Float,
    val sweepAngle: Float
)

@Composable
fun PieChart3D(
    summaries: List<CategorySummary>,
    selectedCategory: ExpenseCategory?,
    onSelectCategory: (ExpenseCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeSlices = summaries.filter { it.amount > 0 && it.percentage > 0 }

    // Clockwise angles starting at -90 deg (12 o'clock):
    // Food (42%): -90° .. +61.2° (right & front-right)
    // Travel (28%): +61.2° .. +162° (front-bottom)
    // Groceries (18%): +162° .. +226.8° (left)
    // Others (12%): +226.8° .. +270° (top-left)
    var currentAngle = -90f
    val sliceAngles = activeSlices.map { summary ->
        val sweep = (summary.percentage / 100f) * 360f
        SliceAngles(summary, currentAngle, sweep).also {
            currentAngle += sweep
        }
    }

    val selectedPop by animateFloatAsState(
        targetValue = if (selectedCategory != null) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 380f),
        label = "slicePop"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.26f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(sliceAngles, selectedCategory) {
                    detectTapGestures { tapOffset ->
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        val cx = w / 2f
                        val cy = h * 0.41f
                        val rx = w * 0.43f
                        val ry = rx * 0.68f
                        val dx = (tapOffset.x - cx) / rx
                        val dy = (tapOffset.y - cy) / ry
                        val dist = hypot(dx, dy)
                        if (dist <= 1.2f) {
                            var deg = (atan2(dy, dx) * 180.0 / PI).toFloat()
                            if (deg < -90f) deg += 360f
                            val tapped = sliceAngles.firstOrNull { slice ->
                                val end = slice.startAngle + slice.sweepAngle
                                deg >= slice.startAngle && deg <= end
                            }?.summary?.category
                            onSelectCategory(if (tapped == selectedCategory) null else tapped)
                        } else {
                            onSelectCategory(null)
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h * 0.405f
            val rx = w * 0.425f
            val ry = rx * 0.68f
            val depth = h * 0.195f // Thick, prominent 3D extrusion matching Reference Image 2

            if (sliceAngles.isEmpty()) {
                drawOval(
                    color = Color(0xFFD5E2F7),
                    topLeft = Offset(cx - rx, cy - ry + depth),
                    size = Size(rx * 2f, ry * 2f)
                )
                drawOval(
                    color = Color(0xFFEEF4FF),
                    topLeft = Offset(cx - rx, cy - ry),
                    size = Size(rx * 2f, ry * 2f)
                )
                return@Canvas
            }

            // 1. Deep, realistic elliptical drop shadow beneath the 3D cylinder
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E3A7A).copy(alpha = 0.44f),
                        Color(0xFF4C6FB8).copy(alpha = 0.22f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy + depth + ry * 0.22f),
                    radius = rx * 1.18f
                ),
                topLeft = Offset(cx - rx * 1.08f, cy - ry * 0.82f + depth + 8f),
                size = Size(rx * 2.16f, ry * 2.05f)
            )

            // Subtle baseline separation so each segment's 3D cut wall is distinctly visible
            fun sliceOffset(slice: SliceAngles): Offset {
                val midRad = ((slice.startAngle + slice.sweepAngle / 2f) * PI / 180.0)
                val baseExplode = w * 0.0085f
                val extraPop = if (selectedCategory == slice.summary.category) w * 0.028f * selectedPop else 0f
                val totalShift = baseExplode + extraPop
                return Offset(
                    (cos(midRad) * totalShift).toFloat(),
                    (sin(midRad) * totalShift * 0.72f).toFloat()
                )
            }

            fun sidePalette(cat: ExpenseCategory): List<Color> = when (cat) {
                ExpenseCategory.FOOD -> listOf(
                    Color(0xFFE86F00),
                    Color(0xFFC85200),
                    Color(0xFF9C3B00)
                )
                ExpenseCategory.TRAVEL -> listOf(
                    Color(0xFF1464F6),
                    Color(0xFF0B46C4),
                    Color(0xFF062E8E)
                )
                ExpenseCategory.GROCERIES -> listOf(
                    Color(0xFF19B500),
                    Color(0xFF0E8700),
                    Color(0xFF075900)
                )
                ExpenseCategory.OTHERS -> listOf(
                    Color(0xFF7B28E0),
                    Color(0xFF5A16B0),
                    Color(0xFF3E0B82)
                )
            }

            fun topPalette(cat: ExpenseCategory): List<Color> = when (cat) {
                ExpenseCategory.FOOD -> listOf(
                    Color(0xFFFFBC2C),
                    Color(0xFFFF9800),
                    Color(0xFFF57600)
                )
                ExpenseCategory.TRAVEL -> listOf(
                    Color(0xFF5AB2FF),
                    Color(0xFF2185FF),
                    Color(0xFF0E5CE6)
                )
                ExpenseCategory.GROCERIES -> listOf(
                    Color(0xFF6BF230),
                    Color(0xFF28D10B),
                    Color(0xFF14A800)
                )
                ExpenseCategory.OTHERS -> listOf(
                    Color(0xFFB66DFF),
                    Color(0xFF8C3BF0),
                    Color(0xFF6B1FD1)
                )
            }

            // Sort slices back-to-front by vertical center (y) for proper Painter's Algorithm 3D occlusion
            val backToFrontSlices = sliceAngles.sortedBy { slice ->
                val midRad = (slice.startAngle + slice.sweepAngle / 2f) * PI / 180.0
                sin(midRad)
            }

            // 2. Draw inner radial cut walls and outer curved cylinder walls for each slice
            backToFrontSlices.forEach { slice ->
                val offset = sliceOffset(slice)
                val sx = cx + offset.x
                val sy = cy + offset.y
                val topRect = Rect(sx - rx, sy - ry, sx + rx, sy + ry)
                val botRect = Rect(sx - rx, sy - ry + depth, sx + rx, sy + ry + depth)

                val startDeg = slice.startAngle
                val endDeg = slice.startAngle + slice.sweepAngle
                val startRad = startDeg * PI / 180.0
                val endRad = endDeg * PI / 180.0

                val sideColors = sidePalette(slice.summary.category)

                // Draw radial cut wall at startAngle
                val startWallPath = Path().apply {
                    moveTo(sx, sy)
                    lineTo(sx + rx * cos(startRad).toFloat(), sy + ry * sin(startRad).toFloat())
                    lineTo(sx + rx * cos(startRad).toFloat(), sy + ry * sin(startRad).toFloat() + depth)
                    lineTo(sx, sy + depth)
                    close()
                }
                drawPath(
                    path = startWallPath,
                    color = sideColors.last()
                )

                // Draw radial cut wall at endAngle
                val endWallPath = Path().apply {
                    moveTo(sx, sy)
                    lineTo(sx + rx * cos(endRad).toFloat(), sy + ry * sin(endRad).toFloat())
                    lineTo(sx + rx * cos(endRad).toFloat(), sy + ry * sin(endRad).toFloat() + depth)
                    lineTo(sx, sy + depth)
                    close()
                }
                drawPath(
                    path = endWallPath,
                    color = sideColors[1]
                )

                // Draw outer curved cylinder wall across front-visible intervals [0..180] and [360..540]
                val frontIntervals = listOf(0f to 180f, 360f to 540f)
                frontIntervals.forEach { (fStart, fEnd) ->
                    val interStart = maxOf(startDeg, fStart)
                    val interEnd = minOf(endDeg, fEnd)
                    if (interEnd > interStart) {
                        val sRad = interStart * PI / 180.0
                        val eRad = interEnd * PI / 180.0
                        val sidePath = Path().apply {
                            moveTo(sx + rx * cos(sRad).toFloat(), sy + ry * sin(sRad).toFloat())
                            arcTo(topRect, interStart, interEnd - interStart, false)
                            lineTo(sx + rx * cos(eRad).toFloat(), sy + ry * sin(eRad).toFloat() + depth)
                            arcTo(botRect, interEnd, -(interEnd - interStart), false)
                            close()
                        }
                        drawPath(
                            path = sidePath,
                            brush = Brush.verticalGradient(
                                colors = sideColors,
                                startY = sy,
                                endY = sy + ry + depth
                            )
                        )
                        // Subtle bottom rim dark line for crisp 3D cylinder definition
                        val botArcPath = Path().apply {
                            arcTo(botRect, interStart, interEnd - interStart, true)
                        }
                        drawPath(
                            path = botArcPath,
                            color = sideColors.last().copy(alpha = 0.8f),
                            style = Stroke(width = 1.5f)
                        )
                    }
                }

                // 3. Draw top elliptical slice face with 3D lighting gradient and specular rim bevel
                val topPath = Path().apply {
                    moveTo(sx, sy)
                    arcTo(topRect, startDeg, slice.sweepAngle, false)
                    close()
                }
                val tColors = topPalette(slice.summary.category)
                drawPath(
                    path = topPath,
                    brush = Brush.radialGradient(
                        colors = tColors,
                        center = Offset(sx - rx * 0.20f, sy - ry * 0.28f),
                        radius = rx * 1.38f
                    )
                )

                // Specular highlight along the outer arc & cut edges of the top face
                drawPath(
                    path = topPath,
                    color = Color.White.copy(alpha = 0.28f),
                    style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                )
            }

            // 4. Draw crisp bold white percentage labels matching Reference Image 2
            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                setShadowLayer(5f, 0f, 2f, android.graphics.Color.argb(115, 0, 0, 0))
            }

            sliceAngles.forEach { slice ->
                if (slice.summary.percentage >= 5) {
                    val offset = sliceOffset(slice)
                    val sx = cx + offset.x
                    val sy = cy + offset.y
                    val midAngle = (slice.startAngle + slice.sweepAngle / 2f) * PI / 180.0
                    val labelDistFactor = when {
                        slice.summary.percentage < 15 -> 0.64f
                        slice.summary.percentage > 35 -> 0.56f
                        else -> 0.58f
                    }
                    val lx = sx + rx * labelDistFactor * cos(midAngle).toFloat()
                    val ly = sy + ry * labelDistFactor * sin(midAngle).toFloat()
                    labelPaint.textSize = (w * 0.062f).coerceIn(32f, 56f)
                    val baselineOffset = (labelPaint.descent() + labelPaint.ascent()) / 2f
                    drawContext.canvas.nativeCanvas.drawText(
                        "${slice.summary.percentage}%",
                        lx,
                        ly - baselineOffset,
                        labelPaint
                    )
                }
            }
        }
    }
}
