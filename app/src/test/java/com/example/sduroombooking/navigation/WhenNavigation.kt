package com.example.sduroombooking.navigation

import com.tngtech.jgiven.Stage

open class WhenNavigation: Stage<WhenNavigation>()
{
    var clickCount = 0
    var isOnBookingPage = false

    fun the_student_clicks_the_booking_button_in_the_navbar(): WhenNavigation
    {
        clickCount += 1
        isOnBookingPage = true
        return self()
    }

    fun the_student_seaches_randomly_without_finding_the_button(): WhenNavigation
    {
        clickCount += 5
        isOnBookingPage = false
        return self()
    }
}