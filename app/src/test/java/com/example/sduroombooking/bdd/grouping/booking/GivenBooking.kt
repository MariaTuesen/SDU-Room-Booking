package com.example.sduroombooking.bdd.grouping.booking

import com.example.sduroombooking.dataclasses.Room
import com.example.sduroombooking.viewmodel.BookingViewModel
import com.example.sduroombooking.viewmodel.RoomsViewModel
import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ScenarioStage

open class GivenBooking : Stage<GivenBooking>()
{
    @ScenarioStage
    lateinit var bookingVM: BookingViewModel
    lateinit var roomsVM: RoomsViewModel

    var selectedRoomId: Int = 0

    fun a_room_is_available_to_book(roomId: Int): GivenBooking
    {
        selectedRoomId = roomId
        return self()
    }

    fun a_list_of_rooms_exists(): GivenBooking
    {
        val rooms = listOf(
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
        return self()
    }

}