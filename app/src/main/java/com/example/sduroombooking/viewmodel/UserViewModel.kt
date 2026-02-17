package com.example.sduroombooking.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sduroombooking.apisetup.RetrofitClient
import com.example.sduroombooking.dataclasses.LoginRequest
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.dataclasses.UserCreate
import kotlinx.coroutines.launch

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
}