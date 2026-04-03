package com.example.sduroombooking.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sduroombooking.apisetup.RetrofitClient
import com.example.sduroombooking.dataclasses.Booking
import com.example.sduroombooking.dataclasses.CreateBookingRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class BookingViewModel : ViewModel() {

    var bookingsForSelectedDate = mutableStateOf<List<Booking>>(emptyList())
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
}