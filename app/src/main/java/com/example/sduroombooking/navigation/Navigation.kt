package com.example.sduroombooking.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val label: String,
    val contentDescription: String

){
    HOME("home", "Home", "Home"),
    PROFILE("profile", "Profile", "Profile"),
    LOGIN("login", "Login", "Login"),
    CREATEACCOUNT("createAccount", "CreateAccount", "Create Account"),
    SEARCHPEOPLE("searchPeople", "SearchPeople", "Search People"),
    TERMSANDCONDITIONS("termsAndConditions", "TermsAndConditions", "Terms and Conditions")
}
enum class NavIcon(
    var destination: Destination,
    val icon: ImageVector,
){
    HOME(Destination.HOME, Icons.Outlined.Home),
    SETTINGS(Destination.PROFILE, Icons.Outlined.Settings),
}