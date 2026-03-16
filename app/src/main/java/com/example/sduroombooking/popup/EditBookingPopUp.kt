package com.example.sduroombooking.popup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LightingColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sduroombooking.R
import com.example.sduroombooking.cards.BookedRoomCard
import com.example.sduroombooking.dataclasses.Booking
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookingPopUp(
    booking: com.example.sduroombooking.dataclasses.Booking,
    room: com.example.sduroombooking.dataclasses.Room?,
    userVM: com.example.sduroombooking.viewmodel.UserViewModel,
    onDismiss: () -> Unit

    )
{

    var peopleQuery by remember { mutableStateOf("") }
    var peopleExpanded by remember { mutableStateOf(false) }

    val allUsers = userVM.allUsers.value
    val participants = remember(booking.userIds, allUsers) {
        allUsers.filter { it.id in booking.userIds }
    }

    val candidatePeople by remember(peopleQuery, allUsers, booking.userIds) {
        derivedStateOf {
            val q = peopleQuery.trim().lowercase()
            allUsers.filter {
                it.id !in booking.userIds &&
                        (it.fullName.lowercase().contains(q) || it.email.lowercase().contains(q))
            }
        }
    }

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
                        Text(
                            text = room?.name ?: "Unknown Room",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            Icons.Default.CalendarMonth,
                            null,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(text = " ${booking.date} ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Icon(
                            Icons.Default.WatchLater,
                            null,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(text = " ${booking.startTime}-${booking.endTime}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = room?.let { "${it.building}, ${it.location}" }?: "",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End)
                {
                    StatusButton(
                        text = "Cancel booking",
                        color = Color.Red,
                        onClick = {
                            userVM.deleteBooking(
                                bookingId = booking.id,
                                onSuccess = {
                                    onDismiss()
                                },
                                onError = {}
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StatusButton(
                        text = "Report issue",
                        color = Color(0xFFFFC107),
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp))
            {
                InfoBadge(
                    text = "${room?.seats ?: ""} seats")
                if (room?.has_monitor == true) InfoBadge(text = "TV screen")
                if (room?.has_whiteboard == true) InfoBadge(text = "Whiteboard")
                if (room?.is_accessible == true) InfoBadge(text = "acc")
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = peopleExpanded,
                onExpandedChange = { peopleExpanded = it }
            ) {
                OutlinedTextField(
                    value = peopleQuery,
                    onValueChange = { peopleQuery = it; peopleExpanded = true },
                    placeholder = { Text("Add people", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(2.dp, AppGreen, RoundedCornerShape(14.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White.copy(alpha = 0.5f)
                    )
                )

                ExposedDropdownMenu(expanded = peopleExpanded, onDismissRequest = { peopleExpanded = false})
                {
                    candidatePeople.take(5).forEach { user ->
                        DropdownMenuItem(
                            text = {Text(user.fullName) },
                            onClick = {
                                val newUserList = booking.userIds + user.id
                                userVM.updateBookingParticipants(booking, newUserList)
                                {
                                    userVM.fetchBookingsForDate(booking.date)
                                }
                                peopleQuery = ""; peopleExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            participants.forEach { user ->
                ParticipantItem(
                    user =user,
                    onRemove = {
                        val newUserList = booking.userIds.filter { it != user.id }
                        userVM.updateBookingParticipants(booking, newUserList)
                        {
                            userVM.fetchBookingsForDate(booking.date)
                        }

                    }
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    color = Color.Gray.copy(alpha = 0.3f)
                )
            }

        }
    }
}

@Composable
fun ParticipantItem(user: com.example.sduroombooking.dataclasses.User, onRemove: () -> Unit)
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
    {
        AsyncImage(
            model = user.profile_picture ?: R.drawable.no_profile_image,
            contentDescription = null,
            modifier = Modifier.size(45.dp).clip(CircleShape).background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f))
        {
            Text(
                text = user.fullName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                text = user.email,
                fontSize = 11.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        IconButton(onClick = onRemove)
        {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun StatusButton(text: String, color: Color, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.5.dp, color),
        color = Color.LightGray.copy(alpha = 0.5f),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(text = text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InfoBadge(text: String)
{
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray
    ) {
        Text(text = text, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
