package com.example.sduroombooking.popup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.PopupGrey

@Composable
fun DeleteAccountConfirmationPopup(
    userEmail: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    var confirmationText by remember { mutableStateOf("") }
    val canDelete = confirmationText.trim() == "DELETE"
    val deleteRed = Color(0xFFD32F2F)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val shape = RoundedCornerShape(16.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .border(2.dp, deleteRed, shape)
                .background(PopupGrey, shape)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Delete account?",
                    style = MaterialTheme.typography.headlineSmall.copy(fontFamily = AlatsiFont),
                    color = deleteRed,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Type DELETE to confirm deletion of $userEmail",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmationText,
                    onValueChange = { confirmationText = it },
                    singleLine = true,
                    label = { Text("Type DELETE") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = deleteRed,
                        unfocusedBorderColor = deleteRed.copy(alpha = 0.5f),
                        focusedLabelColor = deleteRed,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = deleteRed,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppGreen
                        ),
                        border = BorderStroke(1.dp, AppGreen)
                    ) {
                        Text("Cancel", fontFamily = AlatsiFont)
                    }

                    Button(
                        onClick = onConfirmDelete,
                        enabled = canDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canDelete) deleteRed else Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete", color = Color.Black, fontFamily = AlatsiFont)
                    }
                }
            }
        }
    }
}