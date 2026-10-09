package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.ui.theme.LocalHaynerColors

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    language: AppLanguage,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmColor: Color? = null
) {
    val themeColors = LocalHaynerColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = themeColors.textPrimary
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = themeColors.textSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmColor ?: themeColors.primary
                ),
                modifier = Modifier.testTag("confirm_dialog_confirm_button")
            ) {
                Text(Localization.get("btn_confirm", language), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_dialog_cancel_button")
            ) {
                Text(Localization.get("btn_cancel", language))
            }
        },
        containerColor = themeColors.card,
        shape = RoundedCornerShape(20.dp)
    )
}
