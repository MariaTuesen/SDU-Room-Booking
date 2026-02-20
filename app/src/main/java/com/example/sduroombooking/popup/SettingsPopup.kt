package com.example.sduroombooking.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.dp
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey

@Composable
fun SettingsPopup(
    onDismiss: () -> Unit,
    onDeleteAccount: () -> Unit,
    onTerms: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {

        val shape = RoundedCornerShape(16.dp)
        val borderWidth = 2.dp

            Box(
                modifier = Modifier
                    .padding(borderWidth)
                    .background(TextFieldGrey, shape)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Button(
                        onClick = onDeleteAccount,
                        colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Delete account",
                            color = Color.Black,
                            fontFamily = AlatsiFont
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onTerms,
                        colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Read terms and conditions",
                            color = Color.Black,
                            fontFamily = AlatsiFont
                        )
                    }
                }
            }
        }
    }