package com.example.sduroombooking.pages

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sduroombooking.navigation.Destination
import androidx.compose.ui.unit.sp
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey


@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Mail", fontFamily = AlatsiFont, color = TextFieldGrey) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = AppGreen,
                        shape = RoundedCornerShape(14.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppGreen,
                    unfocusedBorderColor = AppGreen,
                    focusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f),
                    unfocusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", fontFamily = AlatsiFont, color = TextFieldGrey) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = AppGreen,
                        shape = RoundedCornerShape(14.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppGreen,
                    unfocusedBorderColor = AppGreen,
                    focusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f),
                    unfocusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = { navController.navigate(Destination.HOME.route) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                modifier = Modifier
                    .width(140.dp)
                    .height(50.dp)
            ) {
                Text(
                    "Login",
                    fontFamily = AlatsiFont,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sign up text
            Row {
                Text("Not a user? ", fontFamily = AlatsiFont, color = Color.Black)

                Text(
                    text = "Sign up", fontFamily = AlatsiFont, color = Color.Blue,
                    modifier = Modifier.clickable {
                        navController.navigate(Destination.CREATEACCOUNT.route)
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(rememberNavController())
    }
}