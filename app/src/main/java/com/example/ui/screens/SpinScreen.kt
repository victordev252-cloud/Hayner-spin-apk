package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.model.SpinMode
import com.example.model.SpinWheel
import com.example.ui.components.SpinWheelCanvas
import com.example.ui.theme.HaynerCyan
import com.example.ui.theme.HaynerGold
import com.example.ui.theme.HaynerPurple
import com.example.ui.theme.LocalHaynerColors
import com.example.ui.theme.SpaceGroteskFont

@Composable
fun SpinScreen(
    activeWheel: SpinWheel,
    allWheels: List<SpinWheel>,
    spinMode: SpinMode,
    isSpinning: Boolean,
    wheelRotation: Float,
    needleDeflection: Float,
    language: AppLanguage,
    onSelectWheel: (SpinWheel) -> Unit,
    onSetSpinMode: (SpinMode) -> Unit,
    onSpin: () -> Unit
) {
    val themeColors = LocalHaynerColors.current
    var showWheelMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.bgBrush)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("spin_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Wheel Selector dropdown & Mode switcher
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Active Wheel Selector Pill
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(themeColors.card)
                        .border(1.dp, themeColors.cardBorder, RoundedCornerShape(20.dp))
                        .clickable(enabled = !isSpinning) { showWheelMenu = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("spin_wheel_selector"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activeWheel.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = SpaceGroteskFont,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Wheel",
                        tint = themeColors.secondary
                    )
                }

                DropdownMenu(
                    expanded = showWheelMenu,
                    onDismissRequest = { showWheelMenu = false },
                    modifier = Modifier.background(themeColors.card)
                ) {
                    allWheels.forEach { wheel ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = wheel.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (wheel.id == activeWheel.id) themeColors.secondary else Color.White
                                )
                            },
                            onClick = {
                                onSelectWheel(wheel)
                                showWheelMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spin Mode Selector Tabs
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(themeColors.card.copy(alpha = 0.6f))
                    .border(1.dp, themeColors.cardBorder, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                ModeChip(
                    title = Localization.get("mode_standard", language),
                    icon = Icons.Default.Star,
                    selected = spinMode == SpinMode.STANDARD,
                    onClick = { if (!isSpinning) onSetSpinMode(SpinMode.STANDARD) },
                    testTag = "mode_standard"
                )
                Spacer(modifier = Modifier.width(4.dp))
                ModeChip(
                    title = Localization.get("mode_practice", language),
                    icon = Icons.Default.School,
                    selected = spinMode == SpinMode.PRACTICE,
                    onClick = { if (!isSpinning) onSetSpinMode(SpinMode.PRACTICE) },
                    testTag = "mode_practice"
                )
                Spacer(modifier = Modifier.width(4.dp))
                ModeChip(
                    title = Localization.get("mode_quick", language),
                    icon = Icons.Default.Bolt,
                    selected = spinMode == SpinMode.QUICK,
                    onClick = { if (!isSpinning) onSetSpinMode(SpinMode.QUICK) },
                    testTag = "mode_quick"
                )
            }

            // Non-monetary practice mode badge
            AnimatedVisibility(
                visible = spinMode == SpinMode.PRACTICE,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(HaynerGold.copy(alpha = 0.15f))
                        .border(1.dp, HaynerGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = HaynerGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = Localization.get("practice_mode_badge", language),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = HaynerGold
                    )
                }
            }
        }

        // Center Wheel Arena
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            SpinWheelCanvas(
                wheel = activeWheel,
                rotationDegrees = wheelRotation,
                needleDeflectionDegrees = needleDeflection,
                modifier = Modifier
                    .fillMaxWidth(0.92f),
                onWheelTap = {
                    if (!isSpinning) onSpin()
                }
            )
        }

        // Bottom Controls: Status label + Prominent Spin Action Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 76.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isSpinning)
                    Localization.get("spinning", language)
                else
                    Localization.get("tap_or_flick", language),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isSpinning) HaynerGold else themeColors.textMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSpin,
                enabled = !isSpinning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .testTag("spin_main_action_button"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColors.primary,
                    disabledContainerColor = themeColors.primary.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isSpinning)
                        Localization.get("spinning", language)
                    else
                        Localization.get("spin_now", language),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ModeChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val themeColors = LocalHaynerColors.current

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) themeColors.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color.White else themeColors.textMuted,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (selected) Color.White else themeColors.textMuted
        )
    }
}
