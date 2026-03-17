package com.example.sduroombooking.dataclasses

data class NotificationItem(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val createdAt: String,
    val read: Boolean = false,
    val type: String? = null,
    val bookingId: String? = null
)