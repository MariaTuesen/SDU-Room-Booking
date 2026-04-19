package com.example.sduroombooking.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sduroombooking.apisetup.RetrofitClient
import com.example.sduroombooking.dataclasses.Booking
import com.example.sduroombooking.dataclasses.CreateBookingRequest
import com.example.sduroombooking.dataclasses.User
import kotlinx.coroutines.launch
import retrofit2.HttpException

class BookingViewModel : ViewModel() {

    var currentUser = mutableStateOf<User?>(null)

    var bookingsForSelectedDate = mutableStateOf<List<Booking>>(emptyList())

    var currentUserBookings = mutableStateOf<List<Booking>>(emptyList())
    var bookingsLoading = mutableStateOf(false)
    var bookingsError = mutableStateOf<String?>(null)

    fun fetchBookingsForDate(date: String) {
        viewModelScope.launch {
            bookingsLoading.value = true
            bookingsError.value = null
            try {
                bookingsForSelectedDate.value = RetrofitClient.api.getBookings(date = date)
            } catch (e: Exception) {
                bookingsError.value = "Failed to fetch bookings: ${e.message}"
                bookingsForSelectedDate.value = emptyList()
            } finally {
                bookingsLoading.value = false
            }
        }
    }

    fun createBooking(
        currentUserId: String,
        roomId: Int,
        date: String,
        startTime: String,
        endTime: String,
        selectedOtherUserIds: List<String>,
        onSuccess: (Booking) -> Unit,
        onError: (String) -> Unit
    ) {
        val allUserIds = (listOf(currentUserId) + selectedOtherUserIds).distinct()

        viewModelScope.launch {
            try {
                val booking = RetrofitClient.api.createBooking(
                    CreateBookingRequest(
                        roomId = roomId,
                        date = date,
                        startTime = startTime,
                        endTime = endTime,
                        userIds = allUserIds
                    )
                )
                onSuccess(booking)
            } catch (e: HttpException) {
                if (e.code() == 409) {
                    onError("Room is already booked in that timeframe")
                } else {
                    onError("Booking failed (HTTP ${e.code()})")
                }
            } catch (e: Exception) {
                onError("Booking failed: ${e.message}")
            }
        }
    }

    fun deleteBooking(
        bookingId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.deleteBooking(bookingId)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("it wasn't possible to cancel booking (HTTP ${response.code()})")
                }
            } catch (e: Exception) {
                onError("Cancellation failed: ${e.message}")
            }
        }
    }

    fun updateBookingParticipants(
        booking: Booking,
        newUserIds: List<String>,
        currentUserId: String,
        onSuccess: () -> Unit
    )
    {
        viewModelScope.launch{
            try
            {
                val updatedBooking = booking.copy(userIds = newUserIds)
                val response = RetrofitClient.api.updateBooking(booking.id, updatedBooking)

                if (newUserIds.isEmpty() || response.isSuccessful)
                {
                    val currentList = currentUserBookings.value.toMutableList()

                    if (newUserIds.contains(currentUserId))
                    {
                        val index = currentList.indexOfFirst { it.id == booking.id }
                        if (index != -1) currentList[index] = updatedBooking
                    } else
                    {
                        currentList.removeAll { it.id == booking.id }
                    }
                    currentUserBookings.value = currentList

                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchUserBookings(userId: String)
    {
        viewModelScope.launch{
            bookingsLoading.value = true
            try
            {
                val allBookings = RetrofitClient.api.getBookings()
                currentUserBookings.value = allBookings.filter { it.userIds.contains(userId) }
            } catch (e: Exception)
            {
                e.printStackTrace()
                bookingsError.value = "Couldn't load your booking"
            } finally
            {
                bookingsLoading.value = false
            }
        }
    }
}