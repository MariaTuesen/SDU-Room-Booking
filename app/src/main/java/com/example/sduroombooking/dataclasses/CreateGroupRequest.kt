package com.example.sduroombooking.dataclasses

data class CreateGroupRequest(
    val name: String,
    val creatorId: String,
    val invitedUserIds: List<String>
)