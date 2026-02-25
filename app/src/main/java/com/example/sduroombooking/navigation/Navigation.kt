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
    SETTINGS("settings", "Settings", "Settings"),
    LOGIN("login", "Login", "Login"),
    CREATEACCOUNT("createAccount", "CreateAccount", "Create Account")
}
enum class NavIcon(
    var destination: Destination,
    val icon: ImageVector,
){
    HOME(Destination.HOME, Icons.Outlined.Home),
    SETTINGS(Destination.SETTINGS, Icons.Outlined.Settings),
}