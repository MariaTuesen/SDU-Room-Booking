package com.example.sduroombooking.bdd.grouping

import com.example.sduroombooking.viewmodel.BookingViewModel
import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ScenarioStage

open class GivenBooking : Stage<GivenBooking>()
{
    @ScenarioStage
    lateinit var bookingVM: BookingViewModel

    var selectedRoomId: Int = 0

    fun a_room_is_available_to_book(roomId: Int): GivenBooking
    {
        selectedRoomId = roomId
        bookingVM = BookingViewModel()
        return self()
    }

}