package com.example.sduroombooking.bdd.grouping.booking

import com.example.sduroombooking.dataclasses.Booking
import com.example.sduroombooking.dataclasses.Room
import com.example.sduroombooking.viewmodel.BookingViewModel
import com.example.sduroombooking.viewmodel.RoomsViewModel
import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ScenarioStage

open class WhenBooking : Stage<WhenBooking>()
{
    @ScenarioStage
    lateinit var bookingVM: BookingViewModel
    lateinit var roomsVM: RoomsViewModel

    fun the_student_books_a_room_and_adds_the_other_students(userIds: List<String>): WhenBooking
    {
        val mockBooking = Booking(
            id = "test-id",
            roomId = 1,
            userIds = userIds,
            date = "24/06/2026",
            startTime = "10:00",
            endTime = "12:00"
        )

        if (userIds.isEmpty())
        {
            bookingVM.bookingsError.value = "Failed to update booking"
        } else {
            bookingVM.currentUserBookings.value = listOf(mockBooking)
            bookingVM.bookingsError.value = null
        }

        return self()
    }


    fun the_student_presses_the_monitor_button(shouldFindResults: Boolean = true): WhenBooking
    {
        val mockRooms = if (shouldFindResults)
        {
            listOf(
                Room(
                    id = 1,
                    name = "Room A",
                    has_monitor = true,
                    has_whiteboard = false,
                    is_accessible = false,
                    seats = 4,
                    location = "Pav 2",
                    building = "Tek"
                ),
                Room(
                    id = 2,
                    name = "Room B",
                    has_monitor = false,
                    has_whiteboard = false,
                    is_accessible = false,
                    seats = 4,
                    location = "Pav 2",
                    building = "Tek"
                )
            )
        } else
        {
            listOf(
                Room(
                    id = 2,
                    name = "Room B",
                    has_monitor = false,
                    has_whiteboard = false,
                    is_accessible = false,
                    seats = 4,
                    location = "Pav 2",
                    building = "Tek"
                )
            )
        }

        roomsVM.allRooms.value = mockRooms.filter { it.has_monitor }
        return self()
    }
}