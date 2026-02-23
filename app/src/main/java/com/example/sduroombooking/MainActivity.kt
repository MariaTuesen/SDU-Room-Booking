package com.example.sduroombooking

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.sduroombooking.pages.SearchPeoplePage
import com.example.sduroombooking.pages.TermsAndConditions
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

    RequestPermissionsOnFirstLaunch()

    LaunchedEffect(Unit) {
        userVM.fetchAllUsers()
    }

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

        composable(Destination.SEARCHPEOPLE.route) {
            SearchPeoplePage(navController = navController, userViewModel = userVM)
        }
        composable(Destination.TERMSANDCONDITIONS.route) {
            TermsAndConditions(navController = navController, userViewModel = userVM)
        }
    }
}

@Composable
fun RequestPermissionsOnFirstLaunch(
    onDone: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    var isFirstLaunch by remember { mutableStateOf(prefs.getBoolean("first_launch", true)) }

    val permissionsToRequest = remember {
        buildList {
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_FINE_LOCATION)

            if (Build.VERSION.SDK_INT >= 33) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        prefs.edit().putBoolean("first_launch", false).apply()
        isFirstLaunch = false
        onDone()
    }

    LaunchedEffect(isFirstLaunch) {
        if (!isFirstLaunch) return@LaunchedEffect

        val missing = permissionsToRequest.filter { perm ->
            ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isEmpty()) {
            prefs.edit().putBoolean("first_launch", false).apply()
            isFirstLaunch = false
            onDone()
        } else {
            launcher.launch(missing.toTypedArray())
        }
    }
}