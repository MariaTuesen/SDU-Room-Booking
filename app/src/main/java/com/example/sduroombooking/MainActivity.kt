package com.example.sduroombooking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sduroombooking.bars.HeaderBar
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.pages.CreateAccount
import com.example.sduroombooking.pages.HomePage
import com.example.sduroombooking.pages.LoginScreen
import com.example.sduroombooking.pages.Profile
import com.example.sduroombooking.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    val userVM: UserViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Destination.LOGIN.route
    ) {

        composable(Destination.LOGIN.route) {
            HeaderBar()
            LoginScreen(navController = navController, userVM = userVM)
        }

        composable(Destination.CREATEACCOUNT.route) {
            HeaderBar()
            CreateAccount(navController = navController, userVM = userVM)
        }

        composable(Destination.HOME.route) {
            HomePage(navController = navController)
        }

        composable(Destination.PROFILE.route) {
            Profile(navController = navController, userViewModel = userVM)
        }
    }
}