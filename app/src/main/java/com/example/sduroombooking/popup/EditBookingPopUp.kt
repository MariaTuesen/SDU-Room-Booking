package com.example.sduroombooking.popup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sduroombooking.cards.BookedRoomCard
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey

@Composable
fun EditBookingPopUp() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, AppGreen),
        colors = CardDefaults.cardColors(TextFieldGrey)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Ø14-504a-3", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(24.dp))
                        Text(text = " 22/2/26 ", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.WatchLater, null, modifier = Modifier.size(24.dp))
                        Text(text = " 12-14", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text = "MMMI, SDU Odense", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }

                Column(horizontalAlignment = Alignment.End) {
                    StatusButton(text = "Cancel booking", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusButton(text = "Report issue", color = Color(0xFFFFC107))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoBadge(text = "5 seats")
                InfoBadge(text = "TV screen")
                InfoBadge(text = "Whiteboard")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            ParticipantItem(name = "Emma Frinkley", email = "emfrin25@student.sdu.dk")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color.Gray)
            ParticipantItem(name = "Julie Holm", email = "juhol25@student.sdu.dk")
        }
    }
}


@Composable
fun StatusButton(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.5.dp, color),
        color = Color.LightGray.copy(alpha = 0.5f),
        modifier = Modifier.clickable { }
    ) {
        Text(text = text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InfoBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray
    ) {
        Text(text = text, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ParticipantItem(name: String, email: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size(40.dp).background(Color.Gray, CircleShape))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontWeight = FontWeight.Bold)
            Text(text = email, fontSize = 12.sp)
        }
        Icon(Icons.Default.Delete, contentDescription = "Remove", modifier = Modifier.clickable { })
    }
}

@Preview()
@Composable
fun EditBookingPopUpPreview() {
    EditBookingPopUp()
}