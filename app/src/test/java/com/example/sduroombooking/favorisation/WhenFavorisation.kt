package com.example.sduroombooking.favorisation

import com.tngtech.jgiven.Stage

open class WhenFavorisation: Stage<WhenFavorisation>()
{
    var isStarFilled = false
    var starMarkedList = mutableListOf<String>()

    fun the_student_favors_another_student(studentName: String, systemShouldSucceed: Boolean = true): WhenFavorisation
    {
        isStarFilled = true

        if (systemShouldSucceed)
        {
            starMarkedList.add(studentName)
        }
        return self()
    }
}