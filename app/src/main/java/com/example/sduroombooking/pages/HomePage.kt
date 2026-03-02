package com.example.sduroombooking.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sduroombooking.bars.NavigationBar
import com.example.sduroombooking.cards.BookedRoomCard
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.viewmodel.UserViewModel

@Composable
fun HomePage(navController: NavHostController) {

    //midlertidig liste mængde
    val myBookings = List(5) { it }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your bookings",
            fontSize = 25.sp,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            contentPadding = PaddingValues(bottom = 5.dp)
        )
        {
            items(myBookings) {
                booking ->
                BookedRoomCard()
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val navController = rememberNavController()
    HomePage(
        navController = navController
    )
}
