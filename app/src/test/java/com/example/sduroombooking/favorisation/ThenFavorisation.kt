package com.example.sduroombooking.favorisation

import com.tngtech.jgiven.Stage
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue

open class ThenFavorisation : Stage<ThenFavorisation>()
{
    fun the_star_icon_gets_filled(isFilled: Boolean): ThenFavorisation
    {
        assertTrue("the star should be filed out", isFilled)
        return self()
    }

    fun the_other_student_gets_added_to_the_list(name: String, list: List<String>): ThenFavorisation
    {
        assertTrue("Failed: Student $name wasn't added to the list", list.contains(name))
        return self()
    }

    fun the_system_does_not_add_the_other_student_tot_the_list(name: String, list: List<String>): ThenFavorisation
    {
        assertFalse("Failed! Student $name was added to the list", list.contains(name))
        return self()
    }
}