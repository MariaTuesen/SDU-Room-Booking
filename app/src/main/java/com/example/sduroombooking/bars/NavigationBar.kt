package com.example.sduroombooking.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import com.example.sduroombooking.R
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.ui.theme.AppGreen

@Composable
fun NavigationBar(navController: NavHostController)
{
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
                    Icon(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "HomeButton",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable{navController.navigate(Destination.HOME.route)}
                    )

                Spacer(modifier = Modifier.width(60.dp))

                IconButton(onClick = {navController.navigate(Destination.SETTINGS.route)})
                {
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "ProfileButton",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(y =(-20).dp)
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
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    val navController = rememberNavController()
    NavigationBar(navController = navController)
}