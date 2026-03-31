package com.example.sduroombooking.dataclasses

data class Group(
    val id: String = "",
    val name: String = "",
    val participants: List<GroupParticipant> = emptyList()
) {
    val memberIds: List<String>
        get() = participants
            .filter { it.status == "accepted" }
            .map { it.userId }

    val memberCount: Int
        get() = participants.count { it.status == "accepted" }

    val pendingUserIds: List<String>
        get() = participants
            .filter { it.status == "pending" }
            .map { it.userId }
}