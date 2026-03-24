package com.example.sduroombooking.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sduroombooking.apisetup.RetrofitClient
import com.example.sduroombooking.dataclasses.Room
import kotlinx.coroutines.launch

class RoomsViewModel : ViewModel() {

    var allRooms = mutableStateOf<List<Room>>(emptyList())
    var roomsLoading = mutableStateOf(false)
    var roomsError = mutableStateOf<String?>(null)

    fun fetchRooms() {
        viewModelScope.launch {
            roomsLoading.value = true
            roomsError.value = null
            try {
                val rooms = RetrofitClient.api.getRooms()
                allRooms.value = rooms
            } catch (e: Exception) {
                roomsError.value = "Failed to fetch rooms: ${e.message}"
            } finally {
                roomsLoading.value = false
            }
        }
    }
}