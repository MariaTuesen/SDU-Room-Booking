package com.example.sduroombooking.popup

import android.provider.Telephony
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sduroombooking.R
import com.example.sduroombooking.cards.BookedRoomCard
import com.example.sduroombooking.cards.BookedRoomUiModel
import com.example.sduroombooking.dataclasses.Booking
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey
data class EditBookingPopUpUiModel(
    val roomDbId: Int,
    val roomName: String,
    val building: String,
    val dateText: String,
    val timeText: String,
    val seatsText: String,
    val hasMonitor: Boolean,
    val hasWhiteboard: Boolean,
    val isAccessible: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookingPopUp(
    model: EditBookingPopUpUiModel,
    booking: Booking,
    userVM: com.example.sduroombooking.viewmodel.UserViewModel,
    onDismiss: () -> Unit

    )
{
    val context = androidx.compose.ui.platform.LocalContext.current

    var peopleQuery by remember { mutableStateOf("") }
    var peopleExpanded by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    var showReportIssue by remember { mutableStateOf(false) }

    val allUsers = userVM.allUsers.value
    val participants by remember(booking.id, userVM.currentUserBookings.value, allUsers) {
        derivedStateOf {
            val freshBooking = userVM.currentUserBookings.value.find { it.id == booking.id }
            val idsToShow = freshBooking?.userIds ?: booking.userIds
            allUsers.filter { it.id in idsToShow }
        }
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
            .fillMaxWidth(0.95f)
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, AppGreen),
        colors = CardDefaults.cardColors(TextFieldGrey)
    )
    {
        Column(modifier = Modifier.padding(20.dp))
        {
                Text(
                    text = model.roomName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = model.building,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        painter = painterResource(R.drawable.calendar),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = model.dateText,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = model.timeText,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                    Spacer(modifier = Modifier.height(16.dp))

                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        item {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        model.seatsText,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }

                        if (model.hasMonitor) {
                            item {
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            "TV screen",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                )
                            }
                        }

                        if (model.hasWhiteboard) {
                            item {
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            "Whiteboard",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                )
                            }
                        }

                        if (model.isAccessible) {
                            item {
                                Icon(
                                    painter = painterResource(R.drawable.wheelchair),
                                    contentDescription = "Accessible",
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(26.dp)
                                        .padding(start = 4.dp)
                                        .offset(y = 11.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = peopleExpanded,
                        onExpandedChange = { peopleExpanded = it }
                    )
                    {
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
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = peopleExpanded,
                            onDismissRequest = { peopleExpanded = false }
                        )
                        {
                            candidatePeople.take(5).forEach { user ->
                                DropdownMenuItem(
                                    text =
                                        {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            {
                                                val baseUrl = "http://10.0.2.2:3000"
                                                val imageUrl = user.profile_picture
                                                    ?.takeIf { it.isNotBlank() }
                                                    ?.let { if (it.startsWith("http")) it else baseUrl + it }

                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(
                                                            imageUrl ?: R.drawable.no_profile_image
                                                        )
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Profile picture",
                                                    modifier = Modifier
                                                        .size(35.dp)
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Text(
                                                    text = user.fullName,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Black
                                                    )
                                                )

                                            }
                                        },
                                    onClick = {
                                        val newUserList = booking.userIds + user.id
                                        userVM.updateBookingParticipants(booking, newUserList)
                                        {
                                        }
                                        peopleQuery = "";
                                        peopleExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp)
                    )
                    {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(participants) { user ->
                                ParticipantItem(
                                    user = user,
                                    onRemove = {
                                        val newList = booking.userIds.filter { it != user.id }
                                        userVM.updateBookingParticipants(booking, newList) { }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusButton(
                            text = "Report issue",
                            color = Color.Yellow,
                            onClick = { showReportIssue = true }
                        )

                        StatusButton(
                            text = "Cancel booking",
                            color = Color.Red,
                            onClick = { showConfirmDelete = true }
                        )
                    }
                }
        }

        if (showConfirmDelete) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showConfirmDelete = false }
            )
            {
                ConfirmDeletePopUp(
                    onConfirm = {
                        userVM.deleteBooking(
                            booking.id,
                            onSuccess = {
                                userVM.fetchUserBookings()
                                showConfirmDelete = false
                                onDismiss()
                            },
                            onError = {}
                        )
                    },
                    onDismiss = { showConfirmDelete = false }
                )
            }
        }

        if (showReportIssue) {
            Dialog(
                onDismissRequest = { showReportIssue = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            )
            {
                ReportIssuePopUp(
                    roomName = model.roomName,
                    onDismiss = { showReportIssue = false },
                    onSend = {
                        android.widget.Toast.makeText(
                            context,
                            "Report sent successfully",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()

                        showReportIssue = false
                    }
                )
            }
        }
    }


@Composable
fun ParticipantItem(user: User, onRemove: () -> Unit)
{
    var showConfirmDeleteParticipant by remember { mutableStateOf(false) }

    val baseUrl = "http://10.0.2.2:3000"

    val imageUrl = user.profile_picture
        ?.takeIf { it.isNotBlank() }
        ?.let { if (it.startsWith("http")) it else "$baseUrl$it" }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
    {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl ?: R.drawable.no_profile_image)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.no_profile_image),
            error = painterResource(R.drawable.no_profile_image),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = user.email, fontSize = 11.sp, color = Color.Gray)
        }

        IconButton(onClick = {  showConfirmDeleteParticipant = true}) {
            Icon(
                painter = painterResource(R.drawable.bin),
                contentDescription = "Remove",
                modifier = Modifier.size(20.dp))
        }
    }

    if (showConfirmDeleteParticipant)
    {
        Dialog(
            onDismissRequest = { showConfirmDeleteParticipant = false},
            properties = DialogProperties(
                usePlatformDefaultWidth = false)
        )
        {
            ConfirmDeleteParticipantPopUp(
                userName = user.fullName,
                onConfirm = {
                    onRemove()
                    showConfirmDeleteParticipant = false
                },
                onDismiss = {showConfirmDeleteParticipant = false}
            )
        }
    }

}

@Composable
fun StatusButton(text: String, color: Color, onClick: () -> Unit)
{
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.5.dp, color),
        color = Color.LightGray.copy(alpha = 0.3f),
        modifier = Modifier.clickable {onClick()}
    )
    {
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}
