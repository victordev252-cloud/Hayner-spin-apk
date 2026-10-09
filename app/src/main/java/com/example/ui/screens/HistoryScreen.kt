package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.model.RewardCategory
import com.example.model.SpinHistoryItem
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.GlassCard
import com.example.ui.theme.HaynerPurple
import com.example.ui.theme.LocalHaynerColors
import com.example.ui.theme.SpaceGroteskFont
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    history: List<SpinHistoryItem>,
    language: AppLanguage,
    onClearHistory: () -> Unit
) {
    val themeColors = LocalHaynerColors.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }
    var sortDescending by remember { mutableStateOf(true) }
    var showClearConfirm by remember { mutableStateOf(false) }

    // Filter & Search
    val filteredList = remember(history, searchQuery, selectedFilter, sortDescending) {
        history.filter { item ->
            val matchesSearch = item.segmentLabel.contains(searchQuery, ignoreCase = true) ||
                    item.wheelName.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "wins" -> item.category != RewardCategory.TRY_AGAIN && item.category != RewardCategory.NO_PRIZE
                "practice" -> item.isPractice
                else -> true
            }

            matchesSearch && matchesFilter
        }.sortedWith { a, b ->
            if (sortDescending) b.timestampMillis.compareTo(a.timestampMillis)
            else a.timestampMillis.compareTo(b.timestampMillis)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.bgBrush)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("history_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Localization.get("history_title", language),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { sortDescending = !sortDescending }) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort",
                        tint = if (sortDescending) themeColors.secondary else Color.White
                    )
                }

                if (history.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearConfirm = true },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = Localization.get("clear_history", language),
                            tint = Color(0xFFF43F5E)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(Localization.get("search_history", language), color = themeColors.textMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = themeColors.textMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColors.secondary,
                unfocusedBorderColor = themeColors.cardBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedFilter == "all",
                    onClick = { selectedFilter = "all" },
                    label = { Text(Localization.get("filter_all", language)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = themeColors.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "wins",
                    onClick = { selectedFilter = "wins" },
                    label = { Text(Localization.get("filter_wins", language)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = themeColors.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "practice",
                    onClick = { selectedFilter = "practice" },
                    label = { Text(Localization.get("filter_practice", language)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = themeColors.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // History Content List or Empty State
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = themeColors.textMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = Localization.get("empty_history_title", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Localization.get("empty_history_desc", language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = themeColors.textMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    HistoryItemCard(item = item)
                }
            }
        }
    }

    if (showClearConfirm) {
        ConfirmDialog(
            title = Localization.get("clear_history", language),
            message = Localization.get("confirm_clear_history", language),
            language = language,
            confirmColor = Color(0xFFF43F5E),
            onConfirm = {
                onClearHistory()
                showClearConfirm = false
            },
            onDismiss = { showClearConfirm = false }
        )
    }
}

@Composable
private fun HistoryItemCard(item: SpinHistoryItem) {
    val themeColors = LocalHaynerColors.current
    val formattedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(item.timestampMillis))
    val itemColor = try { Color(android.graphics.Color.parseColor(item.colorHex)) } catch (_: Exception) { HaynerPurple }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(itemColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.segmentLabel,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = SpaceGroteskFont,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "${item.wheelName} • Spin #${item.spinNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = themeColors.textMuted
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = themeColors.textMuted.copy(alpha = 0.8f)
                    )
                }
            }

            if (item.isPractice) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(themeColors.secondary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "PRACTICE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = themeColors.secondary
                    )
                }
            }
        }
    }
}
