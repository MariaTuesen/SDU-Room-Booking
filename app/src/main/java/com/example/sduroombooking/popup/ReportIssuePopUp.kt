package com.example.sduroombooking.popup

import android.R
import android.text.style.BackgroundColorSpan
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssuePopUp(
    roomName: String,
    onDismiss: () -> Unit,
    onSend: () -> Unit
)
{
    var reportText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(16.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, AppGreen),
        colors = CardDefaults.cardColors(containerColor = TextFieldGrey)
    )
    {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        )
        {
            Text(
                text = "Report issue with $roomName",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Please write the problem you have found with the room in the text box below",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = reportText,
                onValueChange = {reportText = it },
                placeholder = {
                    Text(
                    text = "Write report here...",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    color = Gray
                )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(TextFieldGrey),
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
                )
                {
                    Text(
                        text = "Cancel report",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                        color = Black
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))

                Button(
                    onClick = {onSend()},
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
                )
                {
                    Text(
                        text = "Send report",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                        color = Black
                    )
                }
            }
        }
    }
}