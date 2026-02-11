package com.example.sduroombooking.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.sduroombooking.navigation.Destination

@Composable
fun HomePage(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("This is Homepage Screen", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate(Destination.LOGIN.route) }) {
            Text("Go back to login")
        }
        Button(onClick = { navController.navigate(Destination.SETTINGS.route) }) {
            Text("Go to Settings")
        }

        Button(onClick = { navController.navigate(Destination.BOOKROOM.route) }) {
            Text("Go to Room Booking")
        }

        Button(onClick = { navController.navigate(Destination.SEARCHPEOPLE.route) }) {
            Text("Go to Searching for people")
        }
    }
}
