package com.example.sduroombooking.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.sduroombooking.navigation.Destination

@Composable
fun LoginScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("This is Login Screen", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate(Destination.CREATEACCOUNT.route) }) {
            Text("Go to Create Account")
        }
        Button(onClick = { navController.navigate(Destination.HOME.route) }) {
            Text("Go to HomePage")
        }
    }
}
