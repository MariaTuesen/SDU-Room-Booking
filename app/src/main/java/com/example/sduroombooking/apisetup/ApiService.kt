package com.example.sduroombooking.apisetup

import com.example.sduroombooking.dataclasses.LoginRequest
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.dataclasses.UserCreate
import com.example.sduroombooking.dataclasses.Room
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import com.example.sduroombooking.dataclasses.Booking
import com.example.sduroombooking.dataclasses.CreateBookingRequest
import retrofit2.http.Query
import com.example.sduroombooking.dataclasses.NotificationItem

data class SignupResponse(
    val message: String,
    val user: User
)

interface ApiService {

    @POST("auth/signup")
    suspend fun signup(@Body user: UserCreate): SignupResponse

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): User

    @GET("users")
    suspend fun getAllUsers(): List<User>

    @Multipart
    @POST("users/{id}/profile-picture")
    suspend fun uploadProfilePicture(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): User

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>

    @GET("users/{id}/friends")
    suspend fun getFriends(@Path("id") id: String): List<User>

    @POST("users/{id}/friends/{friendId}")
    suspend fun addFriend(
        @Path("id") id: String,
        @Path("friendId") friendId: String
    ): Response<Unit>

    @DELETE("users/{id}/friends/{friendId}")
    suspend fun removeFriend(
        @Path("id") id: String,
        @Path("friendId") friendId: String
    ): Response<Unit>

    @GET("rooms")
    suspend fun getRooms(): List<Room>

    @GET("bookings")
    suspend fun getBookings(
        @Query("date") date: String? = null,
        @Query("roomId") roomId: Int? = null
    ): List<Booking>

    @POST("bookings")
    suspend fun createBooking(@Body req: CreateBookingRequest): Booking

    @GET("users/{id}/notifications")
    suspend fun getNotifications(
        @Path("id") id: String
    ): List<NotificationItem>

    @POST("users/{id}/notifications/{notificationId}/read")
    suspend fun markNotificationAsRead(
        @Path("id") id: String,
        @Path("notificationId") notificationId: String
    ): Response<Unit>

    @DELETE("bookings/{id}")
    suspend fun deleteBooking(@Path("id") id: String): Response<Unit>

    @POST("bookings/{id}")
    suspend fun updateBooking(
        @Path("id") id: String,
        @Body booking: Booking
    ): Booking
}