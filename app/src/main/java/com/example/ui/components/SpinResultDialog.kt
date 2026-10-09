package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.model.RewardCategory
import com.example.model.SpinResult
import com.example.ui.theme.HaynerCyan
import com.example.ui.theme.HaynerGold
import com.example.ui.theme.HaynerPurple
import com.example.ui.theme.LocalHaynerColors
import com.example.ui.theme.SpaceGroteskFont
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun SpinResultDialog(
    result: SpinResult,
    language: AppLanguage,
    onSpinAgain: () -> Unit,
    onBackHome: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val themeColors = LocalHaynerColors.current
    val category = result.segment.category
    val isTryAgain = category == RewardCategory.TRY_AGAIN || category == RewardCategory.NO_PRIZE

    val formattedDate = remember(result.timestampMillis) {
        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(result.timestampMillis))
    }

    val segmentColor = try {
        Color(android.graphics.Color.parseColor(result.segment.colorHex))
    } catch (_: Exception) {
        HaynerPurple
    }

    // Confetti / particle animation
    val confettiAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        confettiAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background confetti canvas for celebration
            if (!isTryAgain) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val random = Random(42)
                    val p = confettiAnim.value
                    for (i in 0 until 45) {
                        val startX = random.nextFloat() * size.width
                        val speed = 0.6f + (random.nextFloat() * 0.8f)
                        val y = ((p * speed * size.height) + (i * 25f)) % size.height
                        val color = when (i % 4) {
                            0 -> HaynerGold
                            1 -> HaynerCyan
                            2 -> HaynerPurple
                            else -> Color(0xFFFF007A)
                        }
                        drawCircle(
                            color = color.copy(alpha = 0.85f),
                            radius = (3 + (i % 4) * 2).dp.toPx(),
                            center = Offset(startX, y)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("spin_result_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = themeColors.card),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (isTryAgain) themeColors.cardBorder else HaynerGold),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Badge Header Icon
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (isTryAgain) listOf(Color(0xFF334155), Color(0xFF1E293B))
                                    else listOf(HaynerGold, Color(0xFFE11D48))
                                )
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isTryAgain -> Icons.Default.Replay
                                category == RewardCategory.BONUS -> Icons.Default.AutoAwesome
                                category == RewardCategory.SPECIAL -> Icons.Default.Celebration
                                else -> Icons.Default.Star
                            },
                            contentDescription = "Result Badge",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isTryAgain) Localization.get("result_try_again_title", language)
                        else Localization.get("result_congratulations", language),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = SpaceGroteskFont,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isTryAgain) themeColors.textSecondary else HaynerGold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Winning segment name container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(segmentColor.copy(alpha = 0.22f))
                            .border(1.5.dp, segmentColor, RoundedCornerShape(16.dp))
                            .padding(vertical = 16.dp, horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = result.segment.label,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = SpaceGroteskFont,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Metadata details: Wheel, Spin #, Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = result.wheelName,
                            style = MaterialTheme.typography.bodySmall,
                            color = themeColors.textMuted
                        )
                        Text(
                            text = "${Localization.get("result_spin_num", language)}${result.spinNumber}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = themeColors.secondary
                        )
                    }

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = themeColors.textMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Non-monetary disclaimer
                    Text(
                        text = Localization.get("result_disclaimer", language),
                        style = MaterialTheme.typography.labelSmall,
                        color = themeColors.textMuted.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons: SPIN AGAIN & BACK HOME
                    Button(
                        onClick = onSpinAgain,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("result_spin_again_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary)
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.get("spin_again", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBackHome,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("result_back_home_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(Localization.get("btn_back_home", language))
                        }

                        OutlinedButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "I just spun '${result.wheelName}' on Hayner Spin and won '${result.segment.label}'! 🎯⚡"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Spin Result"))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("result_share_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(Localization.get("btn_share", language))
                        }
                    }
                }
            }
        }
    }
}
