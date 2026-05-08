package com.example.sduroombooking.navigation

import com.tngtech.jgiven.Stage

open class GivenNavigation : Stage<GivenNavigation>()
{
    var currentPage: String = ""

    fun the_student_is_on_the_home_page(): GivenNavigation
    {
        currentPage = "Home"
        return self()
    }
}