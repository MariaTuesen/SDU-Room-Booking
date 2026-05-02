package com.example.sduroombooking.bdd.grouping

import com.tngtech.jgiven.junit.ScenarioTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


class BookingSystemScenarioTest : ScenarioTest<GivenBooking, WhenBooking, ThenBooking>()
{
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup()
    {
        Dispatchers.setMain(testDispatcher)
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

        `when`().the_student_books_a_room_and_adds_the_other_students(listOf("me", "studie-ven-1"))

        then().the_other_students_get_added_to_the_booked_room(listOf("studie-ven-1"))
    }

    @Test
    fun a_student_books_a_room_but_cant_add_their_study_group()
    {
        given().a_room_is_available_to_book(2)

        `when`().the_student_books_a_room_and_adds_the_other_students(emptyList())

        then().a_error_message_appears_with_a_reason("Failed to update booking")
    }
}