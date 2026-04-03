package com.example.sduroombooking.dataclasses

data class CreateBookingRequest(
    val roomId: Int,
    val date: String,
    val startTime: String,
    val endTime: String,
    val userIds: List<String> = emptyList()
)