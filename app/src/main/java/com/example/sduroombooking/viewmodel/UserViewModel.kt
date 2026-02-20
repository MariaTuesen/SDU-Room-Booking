package com.example.sduroombooking.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sduroombooking.apisetup.RetrofitClient
import com.example.sduroombooking.dataclasses.LoginRequest
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.dataclasses.UserCreate
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserViewModel : ViewModel() {

    var currentUser = mutableStateOf<User?>(null)

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
                val createdUser = RetrofitClient.api.signup(user)

                currentUser.value = createdUser
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
                currentUser.value = user
                onSuccess()
            } catch (e: retrofit2.HttpException) {
                onError("Invalid credentials")
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            }
        }
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

}