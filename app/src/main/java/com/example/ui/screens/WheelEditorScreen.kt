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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.model.RewardCategory
import com.example.model.SpinWheel
import com.example.model.WheelSegment
import com.example.ui.components.GlassCard
import com.example.ui.components.SpinWheelCanvas
import com.example.ui.theme.LocalHaynerColors
import com.example.ui.theme.SpaceGroteskFont
import com.example.ui.theme.WheelPalette
import java.util.UUID

@Composable
fun WheelEditorScreen(
    wheel: SpinWheel,
    language: AppLanguage,
    onWheelChange: (SpinWheel) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val themeColors = LocalHaynerColors.current

    val hexPalette = listOf(
        "#E11D48", "#7C3AED", "#0284C7", "#059669",
        "#D97706", "#DB2777", "#4F46E5", "#0891B2",
        "#EA580C", "#65A30D", "#9333EA", "#0D9488"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.bgBrush)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("wheel_editor_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("editor_back_button")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Text(
                text = Localization.get("edit_wheel", language),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )

            Button(
                onClick = onSave,
                enabled = wheel.name.isNotBlank() && wheel.segments.size in 2..16,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("editor_save_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(Localization.get("save_wheel", language), style = MaterialTheme.typography.labelMedium)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Wheel Preview Canvas
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SpinWheelCanvas(
                            wheel = wheel,
                            rotationDegrees = 0f,
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
            }

            // Wheel Name & Description Fields
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = wheel.name,
                        onValueChange = { onWheelChange(wheel.copy(name = it)) },
                        label = { Text(Localization.get("wheel_name", language)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("editor_wheel_name_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColors.secondary,
                            unfocusedBorderColor = themeColors.cardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = wheel.description,
                        onValueChange = { onWheelChange(wheel.copy(description = it)) },
                        label = { Text(Localization.get("wheel_description", language)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("editor_wheel_desc_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColors.secondary,
                            unfocusedBorderColor = themeColors.cardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            }

            // Segments Header & Add Segment Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Localization.get("segments", language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = SpaceGroteskFont,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "${wheel.segments.size}/16 (min 2)",
                            style = MaterialTheme.typography.labelSmall,
                            color = themeColors.textMuted
                        )
                    }

                    if (wheel.segments.size < 16) {
                        Button(
                            onClick = {
                                val nextColor = hexPalette[wheel.segments.size % hexPalette.size]
                                val newSeg = WheelSegment(
                                    id = UUID.randomUUID().toString(),
                                    label = "Reward ${wheel.segments.size + 1}",
                                    colorHex = nextColor,
                                    category = RewardCategory.CUSTOM
                                )
                                onWheelChange(wheel.copy(segments = wheel.segments + newSeg))
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = themeColors.secondary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("editor_add_segment_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = Localization.get("add_segment", language),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Segments Editable List
            itemsIndexed(wheel.segments, key = { _, s -> s.id }) { index, seg ->
                SegmentEditorItem(
                    index = index,
                    segment = seg,
                    canDelete = wheel.segments.size > 2,
                    canMoveUp = index > 0,
                    canMoveDown = index < wheel.segments.size - 1,
                    palette = hexPalette,
                    language = language,
                    onLabelChange = { newLabel ->
                        val updated = wheel.segments.toMutableList()
                        updated[index] = seg.copy(label = newLabel)
                        onWheelChange(wheel.copy(segments = updated))
                    },
                    onColorChange = { newColor ->
                        val updated = wheel.segments.toMutableList()
                        updated[index] = seg.copy(colorHex = newColor)
                        onWheelChange(wheel.copy(segments = updated))
                    },
                    onDelete = {
                        val updated = wheel.segments.toMutableList().apply { removeAt(index) }
                        onWheelChange(wheel.copy(segments = updated))
                    },
                    onMoveUp = {
                        val updated = wheel.segments.toMutableList()
                        val temp = updated[index]
                        updated[index] = updated[index - 1]
                        updated[index - 1] = temp
                        onWheelChange(wheel.copy(segments = updated))
                    },
                    onMoveDown = {
                        val updated = wheel.segments.toMutableList()
                        val temp = updated[index]
                        updated[index] = updated[index + 1]
                        updated[index + 1] = temp
                        onWheelChange(wheel.copy(segments = updated))
                    }
                )
            }
        }
    }
}

@Composable
private fun SegmentEditorItem(
    index: Int,
    segment: WheelSegment,
    canDelete: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    palette: List<String>,
    language: AppLanguage,
    onLabelChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val themeColors = LocalHaynerColors.current
    val segColor = try { Color(android.graphics.Color.parseColor(segment.colorHex)) } catch (_: Exception) { Color.Cyan }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color swatch
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(segColor)
                        .border(1.5.dp, Color.White, CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Segment Label input
                OutlinedTextField(
                    value = segment.label,
                    onValueChange = onLabelChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("segment_label_${segment.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColors.secondary,
                        unfocusedBorderColor = themeColors.cardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Reorder controls
                IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = if (canMoveUp) themeColors.secondary else themeColors.cardBorder, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = if (canMoveDown) themeColors.secondary else themeColors.cardBorder, modifier = Modifier.size(18.dp))
                }

                // Delete button
                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF43F5E), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Color picker chips row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(palette.size) { pIndex ->
                    val hex = palette[pIndex]
                    val isSelected = hex.equals(segment.colorHex, ignoreCase = true)
                    val chipColor = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { Color.Gray }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(chipColor)
                            .border(if (isSelected) 2.5.dp else 1.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
                            .clickable { onColorChange(hex) }
                    )
                }
            }
        }
    }
}
