package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import com.example.ui.strings.LocalizedStrings
import com.example.ui.theme.CardWhite
import com.example.ui.theme.PoppinsFamily
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoFamily
import com.example.ui.theme.SecondaryText
import com.example.util.CurrencyFormatter

fun categoryIcon(category: ExpenseCategory): ImageVector = when (category) {
    ExpenseCategory.FOOD -> Icons.Rounded.Restaurant
    ExpenseCategory.TRAVEL -> Icons.Rounded.DirectionsBus
    ExpenseCategory.GROCERIES -> Icons.Rounded.ShoppingCart
    ExpenseCategory.OTHERS -> Icons.Rounded.Category
}

fun LocalizedStrings.categoryLabel(category: ExpenseCategory): String = when (category) {
    ExpenseCategory.FOOD -> catFood
    ExpenseCategory.TRAVEL -> catTravel
    ExpenseCategory.GROCERIES -> catGroceries
    ExpenseCategory.OTHERS -> catOthers
}

@Composable
fun ExpenseItemCard(
    expense: ExpenseEntity,
    strings: LocalizedStrings,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val category = ExpenseCategory.fromKey(expense.category)
    val cardShape = RoundedCornerShape(18.dp)
    val cardModifier = modifier
        .fillMaxWidth()
        .shadow(
            elevation = 9.dp,
            shape = cardShape,
            ambientColor = Color(0xFF9EB6E6),
            spotColor = Color(0xFFA6C0F0)
        )
        .clip(cardShape)
        .border(
            width = 1.dp,
            color = Color(0xFFE7EFFC),
            shape = cardShape
        )
        .then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        )
        .testTag("expense_card_${expense.id}")

    Surface(
        modifier = cardModifier,
        shape = cardShape,
        color = CardWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Glossy circular category icon badge matching reference
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        ambientColor = category.primaryColor,
                        spotColor = category.primaryColor
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(category.gradientStart, category.gradientEnd)
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.55f),
                                category.gradientEnd
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (category == ExpenseCategory.FOOD) {
                    // Reference Food icon: White spoon on left + fork on right
                    SpoonAndForkIcon(modifier = Modifier.size(25.dp))
                } else {
                    Icon(
                        imageVector = categoryIcon(category),
                        contentDescription = strings.categoryLabel(category),
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = strings.categoryLabel(category),
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.5.sp,
                    lineHeight = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = expense.title,
                    color = SecondaryText,
                    fontFamily = RobotoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.5.sp,
                    lineHeight = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = CurrencyFormatter.formatRupees(expense.amount),
                    color = PrimaryText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 20.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = CurrencyFormatter.formatTime(expense.timestampMillis),
                    color = SecondaryText,
                    fontFamily = RobotoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SpoonAndForkIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Left: Spoon (oval bowl + vertical handle)
        val spoonX = w * 0.31f
        drawOval(
            color = Color.White,
            topLeft = Offset(spoonX - w * 0.14f, h * 0.08f),
            size = Size(w * 0.28f, h * 0.38f)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(spoonX - w * 0.055f, h * 0.40f),
            size = Size(w * 0.11f, h * 0.52f),
            cornerRadius = CornerRadius(w * 0.055f, w * 0.055f)
        )

        // Right: Fork (3 tines + neck + vertical handle)
        val forkX = w * 0.70f
        val tineW = w * 0.052f
        val tineOffsets = listOf(-w * 0.10f, 0f, w * 0.10f)
        tineOffsets.forEach { dx ->
            drawLine(
                color = Color.White,
                start = Offset(forkX + dx, h * 0.10f),
                end = Offset(forkX + dx, h * 0.34f),
                strokeWidth = tineW,
                cap = StrokeCap.Round
            )
        }
        // Fork shoulder
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(forkX - w * 0.125f, h * 0.30f),
            size = Size(w * 0.25f, h * 0.14f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
        )
        // Fork handle
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(forkX - w * 0.055f, h * 0.40f),
            size = Size(w * 0.11f, h * 0.52f),
            cornerRadius = CornerRadius(w * 0.055f, w * 0.055f)
        )
    }
}
