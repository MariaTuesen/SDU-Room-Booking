package com.example.sduroombooking.favorisation

import com.tngtech.jgiven.Stage

open class GivenFavorisation: Stage<GivenFavorisation>()
{
    var foundStudentName: String = ""

    fun a_student_searches_for_and_finds_another_student(name: String): GivenFavorisation
    {
        foundStudentName = name
        return self()
    }
}