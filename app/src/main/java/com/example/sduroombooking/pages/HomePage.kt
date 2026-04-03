package com.example.sduroombooking.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sduroombooking.bars.NavigationBar
import com.example.sduroombooking.cards.BookedRoomCard
import com.example.sduroombooking.cards.BookedRoomUiModel
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.popup.EditBookingPopUp
import com.example.sduroombooking.popup.EditBookingPopUpUiModel
import com.example.sduroombooking.viewmodel.UserViewModel
import java.lang.reflect.Executable
import kotlin.String

@Composable
fun HomePage(navController: NavHostController, userVM: UserViewModel)
{
    val rooms by userVM.allRooms
    val currentUser = userVM.currentUser.value
    val userBookings by userVM.currentUserBookings

    var showPopup by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf<String?>(null) }

   val selectedBooking = remember(selectedId, userBookings) {
       userBookings.find {it.id == selectedId}
   }

    LaunchedEffect(Unit)
    {
        userVM.fetchRooms()
        userVM.fetchUserBookings()
    }

    Box(modifier = Modifier.fillMaxSize())
    {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp))
        {
            Text(
                text = "Your bookings",
                fontSize = 25.sp,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (userBookings.isEmpty())
            {
                Text("No bookings found.",
                    modifier = Modifier.padding(8.dp))
            } else
            {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                )
                {
                    items(userBookings) { booking ->
                        val room = rooms.find { it.id == booking.roomId }

                        val cardModel = BookedRoomUiModel(
                            roomName = room?.name ?: "Unknown",
                            building = room?.building ?: "Unknown Building",
                            dateText = booking.date,
                            timeText = "${booking.startTime}-${booking.endTime}",
                            roomDbId = room?.id ?: 0
                        )
                        BookedRoomCard(
                           model = cardModel,
                            onEditClick = {
                                selectedId = booking.id
                                showPopup = true
                            })
                    }
                }
            }
        }

        if (showPopup && selectedBooking != null)
        {
            val room = rooms.find { it.id == selectedBooking.roomId }
            Dialog(
                onDismissRequest = {showPopup = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                )
                {
                    val cardModel = EditBookingPopUpUiModel(
                        roomName = room?.name ?: "Unknown",
                        building = room?.building ?: "Unknown Building",
                        dateText = selectedBooking.date,
                        timeText = "${selectedBooking.startTime}-${selectedBooking.endTime}",
                        roomDbId = room?.id ?: 0,
                        seatsText = "${room?.seats} seats",
                        hasMonitor = room?.has_monitor ?: false,
                        hasWhiteboard = room?.has_whiteboard ?: false,
                        isAccessible = room?.is_accessible ?: false
                    )
                    EditBookingPopUp(
                        model = cardModel,
                        onDismiss = {
                            showPopup = false
                        },
                        booking = selectedBooking,
                        userVM = userVM,
                    )
                }
            }
        }
    }
}

