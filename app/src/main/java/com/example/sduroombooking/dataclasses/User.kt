package com.example.sduroombooking.dataclasses

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val password: String,
    val profile_picture: String?
)