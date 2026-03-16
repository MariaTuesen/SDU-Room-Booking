package com.example.sduroombooking.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sduroombooking.bars.NavigationBar
import com.example.sduroombooking.cards.BookedRoomCard
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.viewmodel.UserViewModel
import java.lang.reflect.Executable

@Composable
fun HomePage(navController: NavHostController, userVM: UserViewModel)
{
    val rooms by userVM.allRooms
    val currentUser = userVM.currentUser.value

    var filteredBookings by remember{
        mutableStateOf<List<com.example.sduroombooking.dataclasses.Booking>>(
            emptyList()
        )
    }

    var showPopup by remember { mutableStateOf(false) }
    var selectedBooking by remember { mutableStateOf<com.example.sduroombooking.dataclasses.Booking?>(null) }

    LaunchedEffect(Unit)
    {
        userVM.fetchRooms()

        try {
            val allBookings = com.example.sduroombooking.apisetup.RetrofitClient.api.getBookings()
            if (currentUser != null) {
                filteredBookings = allBookings.filter { it.userIds.contains(currentUser.id) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

            if (filteredBookings.isEmpty())
            {
                Text("No bookings found.", modifier = Modifier.padding(8.dp))
            } else
            {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                )
                {
                    items(filteredBookings) { booking ->
                        val room = rooms.find { it.id == booking.roomId }
                        BookedRoomCard(
                            booking = booking,
                            room = room,
                            onEditClick = {
                                selectedBooking = booking
                                showPopup = true
                            })
                    }
                }
            }
        }

    }
}

