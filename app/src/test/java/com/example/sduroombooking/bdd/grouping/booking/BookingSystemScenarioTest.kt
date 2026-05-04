package com.example.sduroombooking.bdd.grouping.booking

import com.example.sduroombooking.viewmodel.BookingViewModel
import com.example.sduroombooking.viewmodel.RoomsViewModel
import com.tngtech.jgiven.junit.ScenarioTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookingSystemScenarioTest : ScenarioTest<GivenBooking, WhenBooking, ThenBooking>()
{
    private lateinit var bookingVM: BookingViewModel
    private lateinit var roomsVM: RoomsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup()
    {
        Dispatchers.setMain(testDispatcher)
        bookingVM = BookingViewModel()
        roomsVM = RoomsViewModel()

        given().bookingVM = bookingVM
        given().roomsVM = roomsVM

        `when`().bookingVM = bookingVM
        `when`().roomsVM = roomsVM

        then().bookingVM = bookingVM
        then().roomsVM = roomsVM
    }

    @After
    fun tearDown()
    {
        Dispatchers.resetMain()
    }

    @Test
    fun a_student_Book_a_room_with_their_study_group()
    {
        given().a_room_is_available_to_book(2)

        `when`().the_student_books_a_room_and_adds_the_other_students(listOf("me", "studie-friend-1"))

        then().the_other_students_get_added_to_the_booked_room(listOf("studie-friend-1"))
    }

    @Test
    fun a_student_books_a_room_but_cant_add_their_study_group()
    {
        given().a_room_is_available_to_book(2)

        `when`().the_student_books_a_room_and_adds_the_other_students(emptyList())

        then().a_error_message_appears_with_a_reason("Failed to update booking")
    }

    @Test
    fun a_student_filters_room_by_monitor_success()
    {
        given().a_list_of_rooms_exists()
        `when`().the_student_presses_the_monitor_button()
        then().the_app_shows_rooms_with_a_monitor_in_it()
    }

    @Test
    fun a_student_filters_rooms_by_monitor_no_results()
    {
        given().a_list_of_rooms_exists()
        `when`().the_student_presses_the_monitor_button(false)
        then().the_app_does_not_show_any_room_with_a_monitor_in_it()
    }
}