package com.example.sduroombooking.validation

fun validateSignup(
    email: String,
    password: String,
    confirmPassword: String,
    acceptedTerms: Boolean
): String? {
    if (!acceptedTerms) return "You must accept the terms and conditions"
    if (!email.endsWith("@student.sdu.dk")) return "Only SDU student emails allowed"
    if (password != confirmPassword) return "Passwords do not match"
    return null
}