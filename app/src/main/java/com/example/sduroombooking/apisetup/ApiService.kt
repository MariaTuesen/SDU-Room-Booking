package com.example.sduroombooking.apisetup

import com.example.sduroombooking.dataclasses.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

data class SignupResponse(
    val message: String,
    val user: User
)

interface ApiService {

    //AUTH
    @POST("auth/signup")
    suspend fun signup(@Body user: UserCreate): SignupResponse

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): User

    //USERS
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

    //ROOMS
    @GET("rooms")
    suspend fun getRooms(): List<Room>

    //BOOKINGS
    @GET("bookings")
    suspend fun getBookings(
        @Query("date") date: String? = null,
        @Query("roomId") roomId: Int? = null
    ): List<Booking>

    @POST("bookings")
    suspend fun createBooking(
        @Body req: CreateBookingRequest
    ): Booking

    @POST("bookings/{id}")
    suspend fun updateBooking(
        @Path("id") id: String,
        @Body booking: Booking
    ): Response<Booking>

    @DELETE("bookings/{id}")
    suspend fun deleteBooking(
        @Path("id") id: String
    ): Response<Unit>

    //NOTIFICATIONS
    @GET("notifications/{userId}")
    suspend fun getNotifications(
        @Path("userId") userId: String
    ): List<NotificationItem>

    @POST("notifications/{userId}/{notificationId}/read")
    suspend fun markNotificationAsRead(
        @Path("userId") userId: String,
        @Path("notificationId") notificationId: String
    ): Response<Unit>

    @DELETE("notifications/{userId}/{notificationId}")
    suspend fun deleteNotification(
        @Path("userId") userId: String,
        @Path("notificationId") notificationId: String
    ): Response<Unit>

    //GROUPS
    @GET("groups/{groupId}")
    suspend fun getGroupById(
        @Path("groupId") groupId: String
    ): Group

    @GET("groups/users/{id}")
    suspend fun getGroups(
        @Path("id") id: String
    ): List<Group>

    @POST("groups")
    suspend fun createGroup(
        @Body req: CreateGroupRequest
    ): Group

    @POST("groups/{groupId}/invite")
    suspend fun inviteToGroup(
        @Path("groupId") groupId: String,
        @Body req: GroupInviteRequest
    ): Response<Unit>

    //GROUP INVITES
    @POST("groups/accept/{userId}/{notificationId}")
    suspend fun acceptGroupInvite(
        @Path("userId") userId: String,
        @Path("notificationId") notificationId: String
    ): Group

    @POST("groups/decline/{userId}/{notificationId}")
    suspend fun declineGroupInvite(
        @Path("userId") userId: String,
        @Path("notificationId") notificationId: String
    ): Response<Unit>

    @DELETE("groups/{groupId}/members/{userId}")
    suspend fun leaveGroup(
        @Path("groupId") groupId: String,
        @Path("userId") userId: String
    ): Response<Unit>
}