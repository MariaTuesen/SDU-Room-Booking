package com.example.sduroombooking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.pages.CreateAccount
import com.example.sduroombooking.pages.HomePage
import com.example.sduroombooking.pages.LoginScreen
import com.example.sduroombooking.pages.Settings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            androidx.compose.material3.MaterialTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destination.LOGIN.route) {

        // Login Screen
        composable(Destination.LOGIN.route) {
            LoginScreen(navController)
        }

        // Create Account Screen
        composable(Destination.CREATEACCOUNT.route) {
            CreateAccount(navController)
        }

        // Home Screen
        composable(Destination.HOME.route) {
            HomePage(navController)
        }

        // Settings Screen
        composable(Destination.SETTINGS.route) {
            Settings(navController)
        }
    }
}