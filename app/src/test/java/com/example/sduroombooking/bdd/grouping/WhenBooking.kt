package com.example.sduroombooking.bdd.grouping

import com.example.sduroombooking.dataclasses.Booking
import com.example.sduroombooking.viewmodel.BookingViewModel
import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ScenarioStage

open class WhenBooking : Stage<WhenBooking>()
{
    @ScenarioStage
    lateinit var bookingVM: BookingViewModel


    var selectedRoomId: Int = 0

    fun the_student_books_a_room_and_adds_the_other_students(userIds: List<String>): WhenBooking
    {
        bookingVM.updateBookingParticipants(
            booking = Booking(
                id = "test-id",
                roomId = selectedRoomId,
                userIds = listOf("me"),
                date = "12/12/2026",
                startTime = "10:00",
                endTime = "12:00"
                ),
            newUserIds = userIds,
            currentUserId = "me",
            onSuccess = {}
        )
        return self()
    }
}