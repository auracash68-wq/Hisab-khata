package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import java.io.File
import java.io.FileOutputStream

data class CategorySummary(
    val category: ExpenseCategory,
    val amount: Int,
    val percentage: Int
)

data class PdfGenerationResult(
    val fileName: String,
    val shareUri: Uri,
    val savedToDownloads: Boolean
)

object PdfReportGenerator {

    fun generateExpenseReport(
        context: Context,
        periodLabel: String,
        totalAmount: Int,
        summaries: List<CategorySummary>,
        expenses: List<ExpenseEntity>
    ): PdfGenerationResult {
        val document = PdfDocument()
        val pageWidth = 595 // A4 width in points (72 dpi)
        val pageHeight = 842

        val navyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#071D4B") }
        val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#155BFF") }
        val saffronPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF8A00") }
        val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#16C900") }
        val bgTintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F7F9FF") }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D6E2FA")
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        val whiteBold = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val yellowBold = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD400")
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subtitleWhite = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D0DBF0")
            textSize = 11f
        }
        val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#101C3D")
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#101C3D")
            textSize = 11.5f
        }
        val bodyBoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#101C3D")
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val mutedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#53658D")
            textSize = 10.5f
        }

        var pageNumber = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas: Canvas = page.canvas

        // Header banner
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 96f, navyPaint)
        canvas.drawRect(0f, 96f, pageWidth.toFloat(), 100f, saffronPaint)
        canvas.drawRect(0f, 100f, pageWidth.toFloat(), 103f, Paint().apply { color = Color.WHITE })
        canvas.drawRect(0f, 103f, pageWidth.toFloat(), 107f, greenPaint)

        canvas.drawText("HISAB", 36f, 46f, whiteBold)
        val hisabWidth = whiteBold.measureText("HISAB ")
        canvas.drawText("KHATA", 36f + hisabWidth, 46f, yellowBold)
        canvas.drawText("Smart Expense Tracker — Expense Report ($periodLabel)", 36f, 70f, subtitleWhite)
        canvas.drawText("Generated: ${CurrencyFormatter.formatDateTime(System.currentTimeMillis())}", 36f, 86f, subtitleWhite)

        // Summary box
        val summaryRect = RectF(36f, 126f, pageWidth - 36f, 198f)
        canvas.drawRoundRect(summaryRect, 12f, 12f, bgTintPaint)
        canvas.drawRoundRect(summaryRect, 12f, 12f, borderPaint)
        canvas.drawText("Total Spent ($periodLabel)", 54f, 152f, mutedPaint)
        val bigTotalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#101C3D")
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(CurrencyFormatter.formatRupees(totalAmount), 54f, 184f, bigTotalPaint)
        canvas.drawText("${expenses.size} transactions", pageWidth - 150f, 174f, bluePaint.apply { textSize = 12f })

        // Category breakdown
        var y = 232f
        canvas.drawText("Category Breakdown", 36f, y, headingPaint)
        y += 18f

        summaries.forEach { summary ->
            val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = when (summary.category) {
                    ExpenseCategory.FOOD -> Color.parseColor("#FF8A00")
                    ExpenseCategory.TRAVEL -> Color.parseColor("#087BFF")
                    ExpenseCategory.GROCERIES -> Color.parseColor("#16C900")
                    ExpenseCategory.OTHERS -> Color.parseColor("#8439E8")
                }
            }
            canvas.drawCircle(44f, y + 6f, 6f, dotPaint)
            canvas.drawText(summary.category.displayName, 62f, y + 10f, bodyBoldPaint)
            canvas.drawText(CurrencyFormatter.formatRupees(summary.amount), 340f, y + 10f, bodyBoldPaint)
            canvas.drawText("${summary.percentage}%", 500f, y + 10f, mutedPaint)
            y += 26f
            canvas.drawLine(36f, y - 6f, pageWidth - 36f, y - 6f, borderPaint)
        }

        // Itemized expenses
        y += 24f
        canvas.drawText("Expense Transactions", 36f, y, headingPaint)
        y += 22f

        canvas.drawText("Date", 36f, y, mutedPaint)
        canvas.drawText("Category", 140f, y, mutedPaint)
        canvas.drawText("Description", 230f, y, mutedPaint)
        canvas.drawText("Amount", 495f, y, mutedPaint)
        y += 10f
        canvas.drawLine(36f, y, pageWidth - 36f, y, borderPaint)
        y += 18f

        expenses.forEach { item ->
            if (y > pageHeight - 60f) {
                document.finishPage(page)
                pageNumber++
                page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                canvas = page.canvas
                y = 48f
            }
            val cat = ExpenseCategory.fromKey(item.category)
            canvas.drawText(CurrencyFormatter.formatShortDate(item.timestampMillis), 36f, y, mutedPaint)
            canvas.drawText(cat.displayName, 140f, y, bodyBoldPaint)
            val clippedTitle = if (item.title.length > 34) item.title.take(31) + "..." else item.title
            canvas.drawText(clippedTitle, 230f, y, bodyPaint)
            canvas.drawText(CurrencyFormatter.formatRupees(item.amount), 495f, y, bodyBoldPaint)
            y += 22f
        }

        document.finishPage(page)

        val fileName = "HisabKhata_${periodLabel.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val cacheFile = File(reportsDir, fileName)
        FileOutputStream(cacheFile).use { out ->
            document.writeTo(out)
        }

        var savedToDownloads = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/HisabKhata")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { out ->
                        cacheFile.inputStream().use { input -> input.copyTo(out) }
                    }
                    savedToDownloads = true
                }
            } catch (_: Exception) {
                savedToDownloads = false
            }
        }

        document.close()

        val shareUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            cacheFile
        )

        return PdfGenerationResult(
            fileName = fileName,
            shareUri = shareUri,
            savedToDownloads = savedToDownloads
        )
    }
}
