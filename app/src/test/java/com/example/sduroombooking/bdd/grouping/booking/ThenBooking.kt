package com.example.sduroombooking.bdd.grouping.booking

import com.example.sduroombooking.viewmodel.BookingViewModel
import com.example.sduroombooking.viewmodel.RoomsViewModel
import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ScenarioStage
import junit.framework.TestCase

open class ThenBooking : Stage<ThenBooking>()
{
    @ScenarioStage
    lateinit var bookingVM: BookingViewModel
    lateinit var roomsVM: RoomsViewModel

    fun the_other_students_get_added_to_the_booked_room(expectedIds: List<String>): ThenBooking {
        val bookings = bookingVM.currentUserBookings.value

        TestCase.assertTrue(
            "The list of bookings is empty - backend may never response",
            bookings.isNotEmpty()
        )

        val participants = bookings.lastOrNull()?.userIds
        expectedIds.forEach { id ->
            TestCase.assertTrue("Student with ID $id missing", participants?.contains(id) == true)
        }
        return self()
    }

    fun a_error_message_appears_with_a_reason(expectedError: String): ThenBooking
    {
        TestCase.assertEquals(expectedError, bookingVM.bookingsError.value)
        return self()
    }

    fun the_app_shows_rooms_with_a_monitor_in_it(): ThenBooking
    {
        val filteredRooms = roomsVM.allRooms.value
        TestCase.assertTrue("The list shouldn't be empty", filteredRooms.isNotEmpty())
        filteredRooms.forEach { room ->
            TestCase.assertTrue("Room ${room.name} should have a monitor", room.has_monitor)
        }
        return self()
    }

    fun the_app_does_not_show_any_room_with_a_monitor_in_it(): ThenBooking
    {
        val filterRooms = roomsVM.allRooms.value
        val hasMonitor = filterRooms.any { it.has_monitor }
        TestCase.assertTrue("There shouldn't be shown a room with a monitor", !hasMonitor)
        return self()
    }
}