package com.example.sduroombooking.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sduroombooking.R
import com.example.sduroombooking.viewmodel.UserViewModel
import com.example.sduroombooking.ui.theme.AlatsiFont

@Composable
fun TermsAndConditions(
    navController: NavController,
    userViewModel: UserViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.back_button),
                contentDescription = "Back",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
                        navController.popBackStack()
                    }
            )
        }

        Text(
            text = "Terms and Conditions",
            style = MaterialTheme.typography.headlineMedium.merge(
                TextStyle(fontFamily = AlatsiFont)
            )
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Her skal vi have skrevet en masse text:)",
            style = TextStyle(fontFamily = AlatsiFont)
        )
    }
}