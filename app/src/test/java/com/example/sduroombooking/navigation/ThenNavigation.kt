package com.example.sduroombooking.navigation

import junit.framework.TestCase.assertTrue

open class ThenNavigation : com.tngtech.jgiven.Stage<ThenNavigation>()
{
    fun the_student_is_still_not_on_the_booking_page(actualClicks: Int): ThenNavigation
    {
        assertTrue("Failed! Student reached the page to fast", actualClicks >= 3)
        return self()
    }

    fun the_student_gets_to_theBooking_page_in_less_than_3_clicks(actualClicks: Int): ThenNavigation
    {
        assertTrue("Failed! Student used to many clicks", actualClicks < 3)
        return self()
    }
}