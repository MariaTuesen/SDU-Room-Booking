package com.example.sduroombooking.dataclasses

data class Room(
    val id: Int,
    val name: String,
    val seats: Int,
    val building: String,
    val location: String,
    val has_monitor: Boolean,
    val has_whiteboard: Boolean,
    val is_accessible: Boolean
)