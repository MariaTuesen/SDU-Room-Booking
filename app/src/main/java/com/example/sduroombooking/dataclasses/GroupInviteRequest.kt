package com.example.sduroombooking.dataclasses

data class GroupInviteRequest(
    val groupId: String,
    val invitedUserId: String,
    val invitedByUserId: String
)