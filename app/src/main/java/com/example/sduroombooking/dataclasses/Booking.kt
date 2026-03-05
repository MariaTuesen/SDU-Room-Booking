package com.example.sduroombooking.dataclasses

data class Booking(
    val id: String,
    val roomId: Int,
    val date: String,
    val startTime: String,
    val endTime: String,
    val userIds: List<String> = emptyList(),
    val createdAt: String? = null
)

data class CreateBookingRequest(
    val roomId: Int,
    val date: String,
    val startTime: String,
    val endTime: String,
    val userIds: List<String> = emptyList()
)