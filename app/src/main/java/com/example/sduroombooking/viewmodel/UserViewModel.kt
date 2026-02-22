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

class UserViewModel : ViewModel() {

    var currentUser = mutableStateOf<User?>(null)

    var allUsers = mutableStateOf<List<User>>(emptyList())
    var usersLoading = mutableStateOf(false)
    var usersError = mutableStateOf<String?>(null)

    private val _friends = mutableStateListOf<User>()
    val friends: List<User> get() = _friends

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
}