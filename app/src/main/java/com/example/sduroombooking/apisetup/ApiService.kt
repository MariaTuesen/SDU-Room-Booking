package com.example.sduroombooking.apisetup

import com.example.sduroombooking.dataclasses.LoginRequest
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.dataclasses.UserCreate
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/signup")
    suspend fun signup(@Body user: UserCreate): User
    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): User
}