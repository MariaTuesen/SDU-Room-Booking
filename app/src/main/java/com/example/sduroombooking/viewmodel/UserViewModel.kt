package com.example.sduroombooking.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sduroombooking.apisetup.RetrofitClient
import com.example.sduroombooking.dataclasses.LoginRequest
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.dataclasses.UserCreate
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import com.example.sduroombooking.dataclasses.Booking
import com.example.sduroombooking.dataclasses.CreateBookingRequest
import com.example.sduroombooking.dataclasses.NotificationItem

class UserViewModel : ViewModel() {

    var currentUser = mutableStateOf<User?>(null)

    var allUsers = mutableStateOf<List<User>>(emptyList())
    var usersLoading = mutableStateOf(false)
    var usersError = mutableStateOf<String?>(null)

    private val _friends = mutableStateListOf<User>()
    val friends: List<User> get() = _friends
    var allRooms = mutableStateOf<List<com.example.sduroombooking.dataclasses.Room>>(emptyList())
    var roomsLoading = mutableStateOf(false)
    var roomsError = mutableStateOf<String?>(null)
    var bookingsForSelectedDate = mutableStateOf<List<Booking>>(emptyList())
    var bookingsLoading = mutableStateOf(false)
    var bookingsError = mutableStateOf<String?>(null)
    var notifications = mutableStateOf<List<NotificationItem>>(emptyList())
    var notificationsLoading = mutableStateOf(false)
    var notificationsError = mutableStateOf<String?>(null)

    fun fetchNotifications() {
        val me = currentUser.value ?: return

        viewModelScope.launch {
            notificationsLoading.value = true
            notificationsError.value = null
            try {
                notifications.value = RetrofitClient.api.getNotifications(me.id)
            } catch (e: Exception) {
                notificationsError.value = "Failed to fetch notifications: ${e.message}"
                notifications.value = emptyList()
            } finally {
                notificationsLoading.value = false
            }
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        val me = currentUser.value ?: return

        viewModelScope.launch {
            try {
                RetrofitClient.api.markNotificationAsRead(me.id, notificationId)
                notifications.value = notifications.value.map {
                    if (it.id == notificationId) it.copy(read = true) else it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val unreadNotificationCount: Int
        get() = notifications.value.count { !it.read }

    fun signup(
        fullName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = UserCreate(fullName, email, password)
                val response = RetrofitClient.api.signup(user)
                setLoggedInUser(response.user)
                onSuccess()
            } catch (e: Exception) {
                onError("Signup failed: ${e.message}")
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = RetrofitClient.api.login(LoginRequest(email, password))
                setLoggedInUser(user)
                onSuccess()
            } catch (e: HttpException) {
                onError("Invalid credentials")
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            }
        }
    }

    private fun setLoggedInUser(user: User) {
        currentUser.value = user
        fetchFriendsFromBackend()
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            usersLoading.value = true
            usersError.value = null
            try {
                val users = RetrofitClient.api.getAllUsers()
                allUsers.value = users
            } catch (e: HttpException) {
                usersError.value = "Failed to fetch users (HTTP ${e.code()})"
            } catch (e: Exception) {
                usersError.value = "Failed to fetch users: ${e.message}"
            } finally {
                usersLoading.value = false
            }
        }
    }

    fun fetchFriendsFromBackend() {
        val me = currentUser.value ?: return

        viewModelScope.launch {
            try {
                val friendUsers = RetrofitClient.api.getFriends(me.id)
                _friends.clear()
                _friends.addAll(friendUsers)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isFriend(userId: String): Boolean = _friends.any { it.id == userId }

    fun toggleFriend(context: Context, user: User) {
        val me = currentUser.value ?: return

        viewModelScope.launch {
            try {
                val alreadyFriend = _friends.any { it.id == user.id }

                if (alreadyFriend) {
                    val res = RetrofitClient.api.removeFriend(me.id, user.id)
                    if (res.isSuccessful) {
                        _friends.removeAll { it.id == user.id }
                    }
                } else {
                    val res = RetrofitClient.api.addFriend(me.id, user.id)
                    if (res.isSuccessful) {
                        _friends.add(user)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearInMemoryFriends() {
        _friends.clear()
    }

    fun uploadProfilePicture(
        context: Context,
        userId: String,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            try {
                val bytes = context.contentResolver
                    .openInputStream(imageUri)
                    ?.use { it.readBytes() }
                    ?: throw Exception("Failed to read image")

                val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())

                val part = MultipartBody.Part.createFormData(
                    name = "file",
                    filename = "profile.png",
                    body = requestBody
                )

                val updatedUser = RetrofitClient.api.uploadProfilePicture(userId, part)
                currentUser.value = updatedUser
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logoutClearUiOnly() {
        currentUser.value = null
        clearInMemoryFriends()
    }

    fun deleteAccount(
        context: Context,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.deleteUser(userId)

                if (!response.isSuccessful) {
                    onError("Failed to delete account (HTTP ${response.code()})")
                    return@launch
                }

                currentUser.value = null
                clearInMemoryFriends()

                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
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
    fun UserViewModel.searchUsers(
        query: String,
        users: List<User>,
        currentUserId: String?,
        excludeIds: Set<String> = emptySet(),
        friendFirst: Boolean = false
    ): List<User> {
        val q = query.trim().lowercase()

        fun User.matchesQuery(): Boolean {
            if (q.isBlank()) return true
            return fullName.lowercase().contains(q) || email.lowercase().contains(q)
        }

        val base = users
            .asSequence()
            .filter { it.id != currentUserId }
            .filter { it.id !in excludeIds }
            .filter { it.matchesQuery() }
            .toList()

        return if (!friendFirst) {
            base
        } else {
            base.sortedWith(
                compareByDescending<User> { isFriend(it.id) }
                    .thenBy { it.fullName.lowercase() }
            )
        }
    }
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
        roomId: Int,
        date: String,
        startTime: String,
        endTime: String,
        selectedOtherUserIds: List<String>,
        onSuccess: (Booking) -> Unit,
        onError: (String) -> Unit
    ) {
        val me = currentUser.value
        if (me == null) {
            onError("You must be logged in to book")
            return
        }

        val allUserIds = (listOf(me.id) + selectedOtherUserIds).distinct()

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
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 409) onError("Room is already booked in that timeframe")
                else onError("Booking failed (HTTP ${e.code()})")
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
        newUSerIds: List<String>,
        onSuccess: () -> Unit
    )
    {
        viewModelScope.launch {
            try {
                val updatedBooking = booking.copy(userIds = newUSerIds)

                RetrofitClient.api.updateBooking(booking.id, updatedBooking)

                onSuccess()
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}