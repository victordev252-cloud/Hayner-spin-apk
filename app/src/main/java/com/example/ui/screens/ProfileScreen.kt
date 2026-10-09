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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.model.UserProfile
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.GlassCard
import com.example.ui.theme.HaynerCyan
import com.example.ui.theme.HaynerGold
import com.example.ui.theme.HaynerPurple
import com.example.ui.theme.LocalHaynerColors
import com.example.ui.theme.SpaceGroteskFont

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    wheelsCount: Int,
    language: AppLanguage,
    onUpdateName: (String) -> Unit,
    onUpdateAvatar: (Int) -> Unit,
    onResetStats: () -> Unit
) {
    val themeColors = LocalHaynerColors.current

    val avatarIcons = listOf<Pair<String, ImageVector>>(
        "Star" to Icons.Default.Star,
        "Fire" to Icons.Default.LocalFireDepartment,
        "Crown" to Icons.Default.MilitaryTech,
        "Trophy" to Icons.Default.EmojiEvents,
        "Rocket" to Icons.Default.RocketLaunch,
        "Diamond" to Icons.Default.Diamond,
        "Bolt" to Icons.Default.Bolt,
        "Sparkle" to Icons.Default.AutoAwesome
    )

    var showEditNameDialog by remember { mutableStateOf(false) }
    var editNameText by remember { mutableStateOf(userProfile.displayName) }
    var showResetStatsConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.bgBrush)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("profile_screen")
    ) {
        Text(
            text = Localization.get("profile_title", language),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Profile Hero Card
            item {
                val currentAvatarIcon = avatarIcons.getOrNull(userProfile.avatarIndex)?.second ?: Icons.Default.Star
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar circle with glow
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(HaynerPurple, HaynerCyan))
                                )
                                .border(2.5.dp, HaynerGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = currentAvatarIcon,
                                contentDescription = "User Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Display Name with edit button
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = userProfile.displayName,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = SpaceGroteskFont,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                            IconButton(onClick = {
                                editNameText = userProfile.displayName
                                showEditNameDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Display Name",
                                    tint = themeColors.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Points badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(HaynerGold.copy(alpha = 0.15f))
                                .border(1.dp, HaynerGold, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = HaynerGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${userProfile.totalRewardPoints} Reward Points",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = HaynerGold
                                )
                            }
                        }
                    }
                }
            }

            // Avatar Selector Grid
            item {
                Text(
                    text = Localization.get("choose_avatar", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    avatarIcons.take(4).forEachIndexed { index, pair ->
                        AvatarChip(
                            icon = pair.second,
                            isSelected = userProfile.avatarIndex == index,
                            onClick = { onUpdateAvatar(index) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    avatarIcons.drop(4).forEachIndexed { index, pair ->
                        val realIndex = index + 4
                        AvatarChip(
                            icon = pair.second,
                            isSelected = userProfile.avatarIndex == realIndex,
                            onClick = { onUpdateAvatar(realIndex) }
                        )
                    }
                }
            }

            // Statistics Grid
            item {
                Text(
                    text = Localization.get("stats_overview", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileStatItem(
                            title = Localization.get("total_spins", language),
                            value = "${userProfile.totalSpins}",
                            icon = Icons.Default.Casino,
                            color = HaynerCyan,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatItem(
                            title = Localization.get("streak_days", language),
                            value = "${userProfile.currentStreak} Days",
                            icon = Icons.Default.LocalFireDepartment,
                            color = HaynerGold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileStatItem(
                            title = Localization.get("best_streak", language),
                            value = "${userProfile.bestStreak} Days",
                            icon = Icons.Default.EmojiEvents,
                            color = themeColors.primary,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatItem(
                            title = Localization.get("wheels_created", language),
                            value = "$wheelsCount",
                            icon = Icons.Default.PieChart,
                            color = themeColors.secondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Reset Statistics Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showResetStatsConfirm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reset_stats_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = Localization.get("reset_stats", language),
                        color = Color(0xFFF43F5E),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = {
                Text(
                    text = Localization.get("edit_name", language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            },
            text = {
                OutlinedTextField(
                    value = editNameText,
                    onValueChange = { editNameText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColors.secondary,
                        unfocusedBorderColor = themeColors.cardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateName(editNameText)
                        showEditNameDialog = false
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary)
                ) {
                    Text(Localization.get("btn_confirm", language), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditNameDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(Localization.get("btn_cancel", language))
                }
            },
            containerColor = themeColors.card,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showResetStatsConfirm) {
        ConfirmDialog(
            title = Localization.get("reset_stats", language),
            message = Localization.get("confirm_reset_stats", language),
            language = language,
            confirmColor = Color(0xFFF43F5E),
            onConfirm = {
                onResetStats()
                showResetStatsConfirm = false
            },
            onDismiss = { showResetStatsConfirm = false }
        )
    }
}

@Composable
private fun AvatarChip(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val themeColors = LocalHaynerColors.current

    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(if (isSelected) themeColors.primary else themeColors.card)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) HaynerGold else themeColors.cardBorder,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else themeColors.textSecondary,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun ProfileStatItem(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalHaynerColors.current
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.bodySmall, color = themeColors.textMuted)
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
    }
}
