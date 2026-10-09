package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.ui.theme.HaynerCyan
import com.example.ui.theme.HaynerGold
import com.example.ui.theme.HaynerPurple
import com.example.ui.theme.LocalHaynerColors
import com.example.ui.theme.SpaceGroteskFont

@Composable
fun OnboardingScreen(
    language: AppLanguage,
    onComplete: () -> Unit
) {
    val themeColors = LocalHaynerColors.current
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        Triple(
            "onboarding_1_title",
            "onboarding_1_desc",
            Icons.Default.Casino
        ),
        Triple(
            "onboarding_2_title",
            "onboarding_2_desc",
            Icons.Default.AutoAwesome
        ),
        Triple(
            "onboarding_3_title",
            "onboarding_3_desc",
            Icons.Default.RocketLaunch
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.bgBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("onboarding_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top row: Skip button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (currentStep < 2) {
                TextButton(
                    onClick = onComplete,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = Localization.get("btn_skip", language),
                        style = MaterialTheme.typography.labelLarge,
                        color = themeColors.textMuted
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Center Content with slide animation
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "onboardingSlide",
            modifier = Modifier.weight(1f)
        ) { step ->
            val (titleKey, descKey, icon) = steps[step]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large Feature Icon Card
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                when (step) {
                                    0 -> listOf(HaynerPurple, HaynerCyan)
                                    1 -> listOf(HaynerCyan, HaynerGold)
                                    else -> listOf(HaynerGold, Color(0xFFE11D48))
                                }
                            )
                        )
                        .border(3.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = Localization.get(titleKey, language),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = Localization.get(descKey, language),
                    style = MaterialTheme.typography.bodyLarge,
                    color = themeColors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        // Bottom Navigation: Dots indicator + Next/Get Started Button
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dots indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isSelected = index == currentStep
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) themeColors.secondary else themeColors.cardBorder)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Button
            Button(
                onClick = {
                    if (currentStep < 2) {
                        currentStep++
                    } else {
                        onComplete()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag(if (currentStep == 2) "onboarding_get_started_button" else "onboarding_next_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColors.primary
                )
            ) {
                Text(
                    text = if (currentStep == 2)
                        Localization.get("btn_get_started", language)
                    else
                        Localization.get("btn_next", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}
