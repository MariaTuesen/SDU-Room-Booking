package com.example.sduroombooking

import com.example.sduroombooking.validation.validateBookingTime
import com.example.sduroombooking.validation.validateBookingInputs
import org.junit.Assert.*
import org.junit.Test

class BookingValidationTest {

    @Test
    fun `fails when end time is before start`() {
        val result = validateBookingTime("14:00", "13:00")
        assertEquals("End time must be after start time", result)
    }

    @Test
    fun `fails when booking is longer than 4 hours`() {
        val result = validateBookingTime("10:00", "15:00")
        assertEquals("You can only reserve up to 4 hours", result)
    }

    @Test
    fun `passes for valid time`() {
        val result = validateBookingTime("10:00", "12:00")
        assertNull(result)
    }

    @Test
    fun `fails when date is missing`() {
        val result = validateBookingInputs(
            date = null,
            startTime = "10:00",
            endTime = "12:00",
            location = "SDU Odense",
            building = "42"
        )
        assertEquals("Please select a date", result)
    }

    @Test
    fun `fails when location is empty`() {
        val result = validateBookingInputs(
            date = "1/1/2026",
            startTime = "10:00",
            endTime = "12:00",
            location = "",
            building = "42"
        )
        assertEquals("Please select location", result)
    }

    @Test
    fun `passes when everything is valid`() {
        val result = validateBookingInputs(
            date = "1/1/2026",
            startTime = "10:00",
            endTime = "12:00",
            location = "SDU Odense",
            building = "42"
        )
        assertNull(result)
    }
}