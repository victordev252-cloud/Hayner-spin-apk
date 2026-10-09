package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.model.AppThemeConfig
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.GlassCard
import com.example.ui.theme.HaynerCyan
import com.example.ui.theme.HaynerGold
import com.example.ui.theme.HaynerPurple
import com.example.ui.theme.LocalHaynerColors
import com.example.ui.theme.SpaceGroteskFont

@Composable
fun SettingsScreen(
    currentTheme: AppThemeConfig,
    language: AppLanguage,
    isSoundEnabled: Boolean,
    isVibrationEnabled: Boolean,
    isAnimationsEnabled: Boolean,
    onSetTheme: (AppThemeConfig) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleVibration: (Boolean) -> Unit,
    onToggleAnimations: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    onResetStats: () -> Unit,
    onResetAllData: () -> Unit,
    onBack: () -> Unit
) {
    val themeColors = LocalHaynerColors.current

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetStatsDialog by remember { mutableStateOf(false) }
    var showResetAllDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf<String?>(null) } // "privacy", "terms", "about"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.bgBrush)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("settings_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Localization.get("settings_title", language),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Section 1: Appearance & Themes
            item {
                Text(
                    text = Localization.get("appearance", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(AppThemeConfig.values()) { themeItem ->
                        val isSelected = themeItem == currentTheme
                        ThemeChipItem(
                            theme = themeItem,
                            isSelected = isSelected,
                            onClick = { onSetTheme(themeItem) }
                        )
                    }
                }
            }

            // Section 2: Preferences (Toggles)
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Sound Effects
                        ToggleSettingRow(
                            title = Localization.get("sound_effects", language),
                            subtitle = Localization.get("sound_desc", language),
                            icon = Icons.Default.VolumeUp,
                            checked = isSoundEnabled,
                            onCheckedChange = onToggleSound,
                            testTag = "toggle_sound"
                        )

                        // Haptic Feedback
                        ToggleSettingRow(
                            title = Localization.get("vibration", language),
                            subtitle = Localization.get("vibration_desc", language),
                            icon = Icons.Default.Vibration,
                            checked = isVibrationEnabled,
                            onCheckedChange = onToggleVibration,
                            testTag = "toggle_vibration"
                        )

                        // Smooth Animations
                        ToggleSettingRow(
                            title = Localization.get("animations", language),
                            subtitle = Localization.get("animations_desc", language),
                            icon = Icons.Default.Animation,
                            checked = isAnimationsEnabled,
                            onCheckedChange = onToggleAnimations,
                            testTag = "toggle_animations"
                        )
                    }
                }
            }

            // Section 3: Language Selector
            item {
                Text(
                    text = Localization.get("language", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = lang == language
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) themeColors.primary else themeColors.card)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) HaynerGold else themeColors.cardBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSetLanguage(lang) }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else themeColors.textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = lang.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else themeColors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Section 4: Info & Legal Sheets
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionSettingRow(
                            title = Localization.get("privacy_policy", language),
                            icon = Icons.Default.Security,
                            onClick = { showInfoDialog = "privacy" }
                        )
                        ActionSettingRow(
                            title = Localization.get("terms", language),
                            icon = Icons.Default.Info,
                            onClick = { showInfoDialog = "terms" }
                        )
                        ActionSettingRow(
                            title = Localization.get("about", language),
                            icon = Icons.Default.Info,
                            onClick = { showInfoDialog = "about" }
                        )
                    }
                }
            }

            // Section 5: Destructive Actions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { showClearHistoryDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_clear_history_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Localization.get("clear_history", language), color = Color(0xFFF43F5E))
                    }

                    OutlinedButton(
                        onClick = { showResetStatsDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_reset_stats_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Localization.get("reset_stats", language), color = Color(0xFFF43F5E))
                    }

                    Button(
                        onClick = { showResetAllDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_reset_all_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B))
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Localization.get("reset_all_data", language), fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Footer info
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hayner Spin • ${Localization.get("version", language)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = themeColors.textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Localization.get("offline_notice", language),
                        style = MaterialTheme.typography.labelSmall,
                        color = themeColors.textMuted.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    // Confirmation Dialogs
    if (showClearHistoryDialog) {
        ConfirmDialog(
            title = Localization.get("clear_history", language),
            message = Localization.get("confirm_clear_history", language),
            language = language,
            confirmColor = Color(0xFFF43F5E),
            onConfirm = {
                onClearHistory()
                showClearHistoryDialog = false
            },
            onDismiss = { showClearHistoryDialog = false }
        )
    }

    if (showResetStatsDialog) {
        ConfirmDialog(
            title = Localization.get("reset_stats", language),
            message = Localization.get("confirm_reset_stats", language),
            language = language,
            confirmColor = Color(0xFFF43F5E),
            onConfirm = {
                onResetStats()
                showResetStatsDialog = false
            },
            onDismiss = { showResetStatsDialog = false }
        )
    }

    if (showResetAllDialog) {
        ConfirmDialog(
            title = Localization.get("reset_all_data", language),
            message = Localization.get("confirm_reset_all", language),
            language = language,
            confirmColor = Color(0xFF991B1B),
            onConfirm = {
                onResetAllData()
                showResetAllDialog = false
            },
            onDismiss = { showResetAllDialog = false }
        )
    }

    // Info Dialogs
    showInfoDialog?.let { type ->
        val title = when (type) {
            "privacy" -> Localization.get("privacy_policy", language)
            "terms" -> Localization.get("terms", language)
            else -> Localization.get("about", language)
        }
        val text = when (type) {
            "privacy" -> "Hayner Spin does not track you or transmit your private data to third-party advertising brokers. All custom wheels, spin logs, and preferences are stored strictly inside your local device storage. You can delete or reset this information anytime."
            "terms" -> "Hayner Spin is designed for entertainment and decision-making purposes. All segments, bonus spins, and rewards are simulated demonstration prizes with zero monetary or real cash value. No deposits, withdrawals, or cash wagering are supported."
            else -> "Hayner Spin v1.0.0\nA modern, futuristic spinning wheel application built with Jetpack Compose & Kotlin.\nFeaturing custom physics, sound synthesis, 6 themes, and bilingual support (English & Somali)."
        }

        AlertDialog(
            onDismissRequest = { showInfoDialog = null },
            title = { Text(text = title, fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text(text = text, style = MaterialTheme.typography.bodyMedium, color = themeColors.textSecondary) },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary)
                ) {
                    Text("OK")
                }
            },
            containerColor = themeColors.card,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ThemeChipItem(
    theme: AppThemeConfig,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val themeColors = LocalHaynerColors.current
    val accentColor = when (theme) {
        AppThemeConfig.PURPLE_GALAXY -> HaynerPurple
        AppThemeConfig.NEON_BLUE -> Color(0xFF0284C7)
        AppThemeConfig.ROYAL_GOLD -> HaynerGold
        AppThemeConfig.EMERALD_NIGHT -> Color(0xFF10B981)
        AppThemeConfig.CRIMSON_NIGHT -> Color(0xFFF43F5E)
        AppThemeConfig.MINIMAL_DARK -> Color(0xFFF4F4F5)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) themeColors.card else themeColors.surface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else themeColors.cardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = theme.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) Color.White else themeColors.textSecondary
            )
        }
    }
}

@Composable
private fun ToggleSettingRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val themeColors = LocalHaynerColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = themeColors.secondary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = themeColors.textMuted)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = themeColors.primary,
                uncheckedThumbColor = themeColors.textMuted,
                uncheckedTrackColor = themeColors.surface
            )
        )
    }
}

@Composable
private fun ActionSettingRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val themeColors = LocalHaynerColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = themeColors.secondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = Color.White)
        }
        Text(text = "›", style = MaterialTheme.typography.titleLarge, color = themeColors.textMuted)
    }
}
