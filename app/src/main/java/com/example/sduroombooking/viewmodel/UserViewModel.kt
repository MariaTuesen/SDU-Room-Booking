package com.example.sduroombooking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import org.json.JSONObject

// --- Retrofit API ---
interface AuthService {
    @POST("auth/signup")
    suspend fun signup(@Body user: Map<String, String>): Response<Map<String, String>>

    @POST("auth/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<Map<String, Any>>
}

// --- ViewModel ---
class UserViewModel : ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authService = retrofit.create(AuthService::class.java)

    fun signup(
        fullName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userMap = mapOf(
                    "fullName" to fullName,
                    "email" to email,
                    "password" to password
                )
                val response = authService.signup(userMap)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        val msg = response.errorBody()?.string()?.let {
                            try { JSONObject(it).getString("message") } catch(e: Exception){ it }
                        } ?: "Error creating account"
                        onError(msg)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Network error: ${e.message}")
                }
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val credentials = mapOf("email" to email, "password" to password)
                val response = authService.login(credentials)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        val msg = response.errorBody()?.string()?.let {
                            try { JSONObject(it).getString("message") } catch(e: Exception){ it }
                        } ?: "Invalid credentials"
                        onError(msg)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Network error: ${e.message}")
                }
            }
        }
    }
}