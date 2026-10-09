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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.model.SpinWheel
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.GlassCard
import com.example.ui.theme.HaynerCyan
import com.example.ui.theme.HaynerGold
import com.example.ui.theme.LocalHaynerColors
import com.example.ui.theme.SpaceGroteskFont

@Composable
fun MyWheelsScreen(
    wheels: List<SpinWheel>,
    activeWheelId: String,
    language: AppLanguage,
    onCreateNew: () -> Unit,
    onEditWheel: (SpinWheel) -> Unit,
    onDuplicateWheel: (SpinWheel) -> Unit,
    onDeleteWheel: (String) -> Unit,
    onResetDefaults: () -> Unit,
    onSelectActive: (SpinWheel) -> Unit
) {
    val themeColors = LocalHaynerColors.current
    var wheelToDelete by remember { mutableStateOf<SpinWheel?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.bgBrush)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("my_wheels_screen")
    ) {
        // Top Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Localization.get("wheels_title", language),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )

            IconButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.testTag("reset_defaults_icon")
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = Localization.get("reset_defaults", language),
                    tint = themeColors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Create New Wheel Action Button
        Button(
            onClick = onCreateNew,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("create_new_wheel_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Localization.get("create_new_wheel", language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wheels List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 76.dp)
        ) {
            items(wheels, key = { it.id }) { wheel ->
                val isActive = wheel.id == activeWheelId
                WheelItemCard(
                    wheel = wheel,
                    isActive = isActive,
                    language = language,
                    onSelect = { onSelectActive(wheel) },
                    onEdit = { onEditWheel(wheel) },
                    onDuplicate = { onDuplicateWheel(wheel) },
                    onDelete = { wheelToDelete = wheel }
                )
            }
        }
    }

    // Delete Confirmation Dialog
    wheelToDelete?.let { wheel ->
        ConfirmDialog(
            title = Localization.get("delete_wheel", language),
            message = "Are you sure you want to delete '${wheel.name}'?",
            language = language,
            confirmColor = Color(0xFFE11D48),
            onConfirm = {
                onDeleteWheel(wheel.id)
                wheelToDelete = null
            },
            onDismiss = { wheelToDelete = null }
        )
    }

    // Reset Defaults Dialog
    if (showResetDialog) {
        ConfirmDialog(
            title = Localization.get("reset_defaults", language),
            message = "Reset all wheels back to original factory presets?",
            language = language,
            onConfirm = {
                onResetDefaults()
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }
}

@Composable
private fun WheelItemCard(
    wheel: SpinWheel,
    isActive: Boolean,
    language: AppLanguage,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val themeColors = LocalHaynerColors.current

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wheel_card_${wheel.id}"),
        shape = RoundedCornerShape(20.dp),
        borderColor = if (isActive) HaynerGold else null,
        borderWidth = if (isActive) 2.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Title + Active Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = wheel.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = SpaceGroteskFont,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    if (wheel.isCustom) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(themeColors.secondary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "CUSTOM",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = themeColors.secondary
                            )
                        }
                    }
                }

                if (isActive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(HaynerGold)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = Localization.get("active_badge", language),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            if (wheel.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = wheel.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = themeColors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Segments Color Preview Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                wheel.segments.take(10).forEach { seg ->
                    val color = try { Color(android.graphics.Color.parseColor(seg.colorHex)) } catch (_: Exception) { HaynerCyan }
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
                if (wheel.segments.size > 10) {
                    Text(
                        text = "+${wheel.segments.size - 10}",
                        style = MaterialTheme.typography.labelSmall,
                        color = themeColors.textMuted
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${wheel.segments.size} ${Localization.get("segments", language).lowercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = themeColors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Actions: Use / Edit / Duplicate / Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isActive) {
                    Button(
                        onClick = onSelect,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary.copy(alpha = 0.8f)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(Localization.get("select_active", language), style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = themeColors.secondary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDuplicate) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = themeColors.textMuted, modifier = Modifier.size(20.dp))
                    }
                    if (wheel.isCustom) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF43F5E), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
