package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.model.DailyChallenge
import com.example.model.ScreenDestination
import com.example.model.SpinHistoryItem
import com.example.model.SpinResult
import com.example.model.SpinWheel
import com.example.model.UserProfile
import com.example.ui.components.GlassCard
import com.example.ui.components.HaynerHeader
import com.example.ui.components.SpinWheelCanvas
import com.example.ui.theme.HaynerCyan
import com.example.ui.theme.HaynerGold
import com.example.ui.theme.HaynerPurple
import com.example.ui.theme.LocalHaynerColors
import com.example.ui.theme.SpaceGroteskFont
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    activeWheel: SpinWheel,
    userProfile: UserProfile,
    lastResult: SpinResult?,
    recentHistory: List<SpinHistoryItem>,
    challenges: List<DailyChallenge>,
    language: AppLanguage,
    onSpinNow: () -> Unit,
    onClaimChallenge: (String) -> Unit,
    onNavigate: (ScreenDestination) -> Unit
) {
    val themeColors = LocalHaynerColors.current

    // Subtle idle floating rotation for the preview wheel
    val idleRotation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        idleRotation.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(24000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.bgBrush)
            .statusBarsPadding()
            .testTag("home_screen")
    ) {
        // App Header
        HaynerHeader(
            streakDays = userProfile.currentStreak,
            onProfileClick = { onNavigate(ScreenDestination.PROFILE) },
            onSettingsClick = { onNavigate(ScreenDestination.SETTINGS) }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Wheel Section
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Wheel Title & Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
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

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(themeColors.primary.copy(alpha = 0.2f))
                                    .border(1.dp, themeColors.primary, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${activeWheel.segments.size} SEGMENTS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = themeColors.secondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interactive Wheel Canvas Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            SpinWheelCanvas(
                                wheel = activeWheel,
                                rotationDegrees = idleRotation.value,
                                modifier = Modifier.size(240.dp),
                                onWheelTap = onSpinNow
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Big Glowing "SPIN NOW" Hero Button
                        Button(
                            onClick = onSpinNow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(12.dp, RoundedCornerShape(18.dp))
                                .testTag("home_spin_now_button"),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeColors.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("spin_now", language),
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

            // Dashboard Stats 2x2 Grid Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card 1: Total Spins
                        StatCard(
                            title = Localization.get("total_spins", language),
                            value = "${userProfile.totalSpins}",
                            icon = Icons.Default.Casino,
                            iconColor = HaynerCyan,
                            modifier = Modifier.weight(1f),
                            testTag = "stat_total_spins"
                        )

                        // Card 2: Spin Streak
                        StatCard(
                            title = Localization.get("streak_days", language),
                            value = "${userProfile.currentStreak} Days",
                            icon = Icons.Default.LocalFireDepartment,
                            iconColor = HaynerGold,
                            modifier = Modifier.weight(1f),
                            testTag = "stat_spin_streak"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card 3: Last Spin Result
                        StatCard(
                            title = Localization.get("last_result", language),
                            value = lastResult?.segment?.label ?: "None yet",
                            icon = Icons.Default.Star,
                            iconColor = themeColors.primary,
                            modifier = Modifier.weight(1f),
                            testTag = "stat_last_result"
                        )

                        // Card 4: Themes
                        StatCard(
                            title = Localization.get("available_themes", language),
                            value = "6 Themes",
                            icon = Icons.Default.Palette,
                            iconColor = themeColors.secondary,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(ScreenDestination.SETTINGS) },
                            testTag = "stat_themes"
                        )
                    }
                }
            }

            // Daily Challenges Section
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Localization.get("daily_challenges", language),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = SpaceGroteskFont,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )

                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = HaynerGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        challenges.forEach { challenge ->
                            ChallengeRow(
                                challenge = challenge,
                                language = language,
                                onClaim = { onClaimChallenge(challenge.id) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Recent Activity Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.get("recent_activity", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = SpaceGroteskFont,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )

                    if (recentHistory.isNotEmpty()) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = themeColors.secondary,
                            modifier = Modifier.clickable { onNavigate(ScreenDestination.HISTORY) }
                        )
                    }
                }
            }

            if (recentHistory.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = Localization.get("no_recent_activity", language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeColors.textMuted,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            } else {
                items(recentHistory.take(4)) { item ->
                    RecentSpinRow(item)
                }
            }

            // Bottom breathing space for floating bottom navigation
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    testTag: String = ""
) {
    val themeColors = LocalHaynerColors.current
    GlassCard(
        modifier = modifier
            .testTag(testTag)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = themeColors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ChallengeRow(
    challenge: DailyChallenge,
    language: AppLanguage,
    onClaim: () -> Unit
) {
    val themeColors = LocalHaynerColors.current
    val progress = (challenge.currentCount.toFloat() / challenge.targetCount).coerceIn(0f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(themeColors.card.copy(alpha = 0.5f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = Localization.get(challenge.titleKey, language),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (challenge.completed) HaynerGold else themeColors.secondary,
                    trackColor = themeColors.cardBorder
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${challenge.currentCount}/${challenge.targetCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = themeColors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (challenge.claimed) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Claimed",
                tint = HaynerGold,
                modifier = Modifier.size(24.dp)
            )
        } else if (challenge.completed) {
            Button(
                onClick = onClaim,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HaynerGold),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = Localization.get("claim", language),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun RecentSpinRow(item: SpinHistoryItem) {
    val themeColors = LocalHaynerColors.current
    val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(item.timestampMillis))
    val itemColor = try {
        Color(android.graphics.Color.parseColor(item.colorHex))
    } catch (_: Exception) {
        HaynerPurple
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(itemColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = item.segmentLabel,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "${item.wheelName} • Spin #${item.spinNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = themeColors.textMuted
                    )
                }
            }

            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelSmall,
                color = themeColors.textMuted
            )
        }
    }
}
