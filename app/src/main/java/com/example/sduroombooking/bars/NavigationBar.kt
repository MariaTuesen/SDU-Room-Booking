package com.example.sduroombooking.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sduroombooking.R
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.viewmodel.UserViewModel

@Composable
fun NavigationBar(navController: NavHostController, userViewModel: UserViewModel)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val user = userViewModel.currentUser.value
    val baseUrl = "http://10.0.2.2:3000"

    val imageUrl = user?.profile_picture
        ?.takeIf { it.isNotBlank() }
        ?.let { if (it.startsWith("http")) it else baseUrl + it }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(101.dp),
        contentAlignment = Alignment.BottomCenter
    )
    {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = AppGreen)
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                val isHome = currentRoute == Destination.HOME.route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable(enabled = !isHome)
                        {
                            navController.navigate(Destination.HOME.route)
                        }
                )
                {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "HomeButton",
                        modifier = Modifier.size(40.dp)
                    )

                    if (isHome)
                    {
                        Box(modifier = Modifier
                            .size(6.dp)
                            .background(Black, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(60.dp))

                val isProfile = currentRoute == Destination.PROFILE.route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable(enabled = !isProfile)
                    {
                        navController.navigate(Destination.PROFILE.route)
                    }
                )
                {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl ?: R.drawable.no_profile_image)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.no_profile_image),
                            error = painterResource(R.drawable.no_profile_image),
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (isProfile) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Black, CircleShape)
                            )
                        }
                    }
                }
            }
        }

        val isBooking = currentRoute == Destination.HOME.route
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset(y = (-20).dp)
                .clickable{navController.navigate(Destination.HOME.route)}
        )
        {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(AppGreen, shape = CircleShape),
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "AddButton",
                    modifier = Modifier
                        .size(65.dp)
                        .border(3.dp, Black, CircleShape),
                )
            }
            if (isBooking)
            {
                Box(modifier = Modifier
                    .padding(top = 4.dp)
                    .size(6.dp)
                    .background(Black, CircleShape)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    val navController = rememberNavController()
    NavigationBar(navController = navController,
        userViewModel = UserViewModel()
    )
}
