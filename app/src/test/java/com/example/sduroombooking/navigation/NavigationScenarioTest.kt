package com.example.sduroombooking.navigation

import com.tngtech.jgiven.junit.ScenarioTest
import org.junit.Test

class NavigationScenarioTest : ScenarioTest<GivenNavigation, WhenNavigation, ThenNavigation>()
{
    @Test
    fun a_student_can_navigate_to_the_booking_page_easily()
    {
        given().the_student_is_on_the_home_page()
        `when`().the_student_clicks_the_booking_button_in_the_navbar()
        then().the_student_gets_to_theBooking_page_in_less_than_3_clicks( actualClicks = `when`().clickCount)
    }

    @Test
    fun a_student_try_to_navigate_to_the_booking_page_but_fails()
    {
        given().the_student_is_on_the_home_page()
        `when`().the_student_seaches_randomly_without_finding_the_button()
        then().the_student_is_still_not_on_the_booking_page(actualClicks = `when`().clickCount)
    }
}