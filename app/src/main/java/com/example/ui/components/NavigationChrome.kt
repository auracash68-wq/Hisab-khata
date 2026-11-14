package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.strings.LocalizedStrings
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CardShadowTint
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.NavInactiveTint
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RobotoFamily

enum class MainNavTab {
    HOME,
    ANALYTICS,
    SETTINGS
}

@Composable
fun HisabBottomNavigationBar(
    selectedTab: MainNavTab,
    strings: LocalizedStrings,
    onSelectTab: (MainNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val navShape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = navShape,
                ambientColor = CardShadowTint,
                spotColor = Color(0xFF93B0EA)
            ),
        shape = navShape,
        color = CardWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(64.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Rounded.Home,
                label = strings.navHome,
                selected = selectedTab == MainNavTab.HOME,
                tag = "nav_home",
                onClick = { onSelectTab(MainNavTab.HOME) },
                modifier = Modifier.weight(1f)
            )
            BottomNavItem(
                icon = Icons.Rounded.BarChart,
                label = strings.navAnalytics,
                selected = selectedTab == MainNavTab.ANALYTICS,
                tag = "nav_analytics",
                onClick = { onSelectTab(MainNavTab.ANALYTICS) },
                modifier = Modifier.weight(1f)
            )
            BottomNavItem(
                icon = Icons.Rounded.Settings,
                label = strings.navSettings,
                selected = selectedTab == MainNavTab.SETTINGS,
                tag = "nav_settings",
                onClick = { onSelectTab(MainNavTab.SETTINGS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (selected) PrimaryBlue else NavInactiveTint
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = PrimaryBlue.copy(alpha = 0.15f)),
                role = Role.Tab,
                onClick = onClick
            )
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = tint,
            fontFamily = RobotoFamily,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            maxLines = 1
        )
    }
}

@Composable
fun FloatingMicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(104.dp),
        contentAlignment = Alignment.Center
    ) {
        // Multi-ring illuminated cyan-blue glow & concentric borders matching Reference Image 2
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val maxR = size.minDimension / 2f

            // 1. Soft wide cyan outer aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CyanGlow.copy(alpha = 0.50f),
                        BrightBlue.copy(alpha = 0.22f),
                        CyanGlow.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = c,
                    radius = maxR
                ),
                radius = maxR,
                center = c
            )

            // 2. Outer translucent cyan halo ring
            drawCircle(
                color = CyanGlow.copy(alpha = 0.24f),
                radius = maxR * 0.84f,
                center = c
            )
            drawCircle(
                color = CyanGlow.copy(alpha = 0.65f),
                radius = maxR * 0.84f,
                center = c,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // 3. Secondary royal-blue ring with crisp white-cyan outline
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(BrightBlue, PrimaryBlue)
                ),
                radius = maxR * 0.72f,
                center = c
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.92f),
                radius = maxR * 0.72f,
                center = c,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // 4. Core glossy royal-blue 3D sphere button with inner white ring
        Box(
            modifier = Modifier
                .size(66.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = PrimaryBlue,
                    spotColor = CyanGlow
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF33A3FF),
                            PrimaryBlue,
                            Color(0xFF0A34B8)
                        )
                    )
                )
                .border(
                    width = 1.8.dp,
                    color = Color.White.copy(alpha = 0.88f),
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .testTag("floating_mic_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = stringResource(R.string.cd_voice_entry),
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
