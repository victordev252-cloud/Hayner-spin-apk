package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.model.ScreenDestination
import com.example.ui.theme.HaynerCyan
import com.example.ui.theme.HaynerGold
import com.example.ui.theme.HaynerPurple
import com.example.ui.theme.LocalHaynerColors

@Composable
fun HaynerBottomNav(
    currentScreen: ScreenDestination,
    language: AppLanguage,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalHaynerColors.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = themeColors.surface.copy(alpha = 0.96f),
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, themeColors.cardBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                NavItem(
                    label = Localization.get("nav_home", language),
                    icon = Icons.Default.Home,
                    selected = currentScreen == ScreenDestination.HOME,
                    testTag = "nav_home",
                    onClick = { onNavigate(ScreenDestination.HOME) }
                )

                // 2. My Wheels
                NavItem(
                    label = Localization.get("nav_wheels", language),
                    icon = Icons.Default.PieChart,
                    selected = currentScreen == ScreenDestination.MY_WHEELS,
                    testTag = "nav_wheels",
                    onClick = { onNavigate(ScreenDestination.MY_WHEELS) }
                )

                // Space for Center Spin
                Spacer(modifier = Modifier.size(56.dp))

                // 4. History
                NavItem(
                    label = Localization.get("nav_history", language),
                    icon = Icons.Default.History,
                    selected = currentScreen == ScreenDestination.HISTORY,
                    testTag = "nav_history",
                    onClick = { onNavigate(ScreenDestination.HISTORY) }
                )

                // 5. Profile
                NavItem(
                    label = Localization.get("nav_profile", language),
                    icon = Icons.Default.Person,
                    selected = currentScreen == ScreenDestination.PROFILE,
                    testTag = "nav_profile",
                    onClick = { onNavigate(ScreenDestination.PROFILE) }
                )
            }

            // Prominent Center SPIN Action
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-14).dp)
                    .size(62.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(HaynerPurple, HaynerCyan))
                    )
                    .border(2.dp, HaynerGold, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = 32.dp),
                        onClick = { onNavigate(ScreenDestination.SPIN) }
                    )
                    .testTag("nav_spin"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RotateRight,
                        contentDescription = "Spin Screen",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = Localization.get("nav_spin", language).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val themeColors = LocalHaynerColors.current
    val contentColor = animateColorAsState(
        targetValue = if (selected) themeColors.secondary else themeColors.textMuted,
        label = "navColor"
    )

    Column(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor.value,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ),
            color = contentColor.value,
            maxLines = 1
        )
    }
}
