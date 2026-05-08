package com.example.sduroombooking.favorisation

import com.tngtech.jgiven.junit.ScenarioTest
import org.junit.Test

class favorisationScenarioTest : ScenarioTest<GivenFavorisation, WhenFavorisation, ThenFavorisation>()
{
    @Test
    fun a_student_marks_others_with_a_star_success()
    {
        val student = "Jon Doe"

        given().a_student_searches_for_and_finds_another_student(student)
        `when`().the_student_favors_another_student(student, systemShouldSucceed = true)
        then().the_star_icon_gets_filled(`when`().isStarFilled).and().the_other_student_gets_added_to_the_list(student, `when`().starMarkedList)
    }

    @Test
    fun a_student_marks_others_with_a_star_but_system_fails()
    {
        val student = "Jon Doe"

        given().a_student_searches_for_and_finds_another_student(student)
        `when`().the_student_favors_another_student(student, systemShouldSucceed = false)
        then().the_star_icon_gets_filled(`when`().isStarFilled).and().the_system_does_not_add_the_other_student_tot_the_list(student, `when`().starMarkedList)
    }
}