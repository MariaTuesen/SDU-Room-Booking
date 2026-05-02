package com.example.sduroombooking.bdd.grouping

import androidx.compose.foundation.isSystemInDarkTheme
import com.example.sduroombooking.viewmodel.BookingViewModel
import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ScenarioStage
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue

open class ThenBooking : Stage<ThenBooking>()
{
    @ScenarioStage
    lateinit var bookingVM: BookingViewModel

    fun the_other_students_get_added_to_the_booked_room(expectedIds: List<String>): ThenBooking
    {
        val participants = bookingVM.currentUser.value
        assertTrue("Listen over deltagere burde ikke være tom", participants.isNotEmpty() == true)
        expectedIds.forEach { id -> assertTrue("Studens with ID $id missing", participantsList?.contains(id) == true) }
        return self()
    }

    fun a_error_message_appears_with_a_reason(expectedError: String): ThenBooking
    {
        assertEquals(expectedError, bookingVM.bookingsError.value)
        return self()
    }
}