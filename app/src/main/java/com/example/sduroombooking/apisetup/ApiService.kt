package com.example.sduroombooking.apisetup

import com.example.sduroombooking.dataclasses.LoginRequest
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.dataclasses.UserCreate
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("auth/signup")
    suspend fun signup(@Body user: UserCreate): User
    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): User
    @Multipart
    @POST("/users/{id}/profile-picture")
    suspend fun uploadProfilePicture(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): User
}