package com.example.sduroombooking

import com.example.sduroombooking.validation.validateSignup
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Test

class SignupValidationTest {

    @Test
    fun `fails when terms not accepted`() {
        val result = validateSignup(
            email = "test@student.sdu.dk",
            password = "123",
            confirmPassword = "123",
            acceptedTerms = false
        )

        Assert.assertEquals("You must accept the terms and conditions", result)
    }

    @Test
    fun `fails when email is not SDU`() {
        val result = validateSignup(
            email = "test@gmail.com",
            password = "123",
            confirmPassword = "123",
            acceptedTerms = true
        )

        Assert.assertEquals("Only SDU student emails allowed", result)
    }

    @Test
    fun `fails when passwords do not match`() {
        val result = validateSignup(
            email = "test@student.sdu.dk",
            password = "123",
            confirmPassword = "456",
            acceptedTerms = true
        )

        Assert.assertEquals("Passwords do not match", result)
    }

    @Test
    fun `passes when everything is correct`() {
        val result = validateSignup(
            email = "test@student.sdu.dk",
            password = "123",
            confirmPassword = "123",
            acceptedTerms = true
        )

        Assert.assertNull(result)
    }
}