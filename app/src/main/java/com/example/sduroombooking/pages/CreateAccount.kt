package com.example.sduroombooking.pages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey

@Composable
fun CreateAccount(navController: NavHostController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Back button at top-left
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 130.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(50.dp)
            )
        }

        // Form centered vertically
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center) // center the whole form vertically
        ) {
            // Full Name field
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = { Text("Full name", fontFamily = AlatsiFont, color = TextFieldGrey) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AppGreen, RoundedCornerShape(14.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppGreen,
                    unfocusedBorderColor = AppGreen,
                    focusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f),
                    unfocusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("SDU Mail", fontFamily = AlatsiFont, color = TextFieldGrey) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AppGreen, RoundedCornerShape(14.dp)),
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
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AppGreen, RoundedCornerShape(14.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppGreen,
                    unfocusedBorderColor = AppGreen,
                    focusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f),
                    unfocusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Confirm password", fontFamily = AlatsiFont, color = TextFieldGrey) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AppGreen, RoundedCornerShape(14.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppGreen,
                    unfocusedBorderColor = AppGreen,
                    focusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f),
                    unfocusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Account button
            Button(
                onClick = { navController.navigate(Destination.HOME.route) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    "Create account",
                    fontFamily = AlatsiFont,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
        }
    }
}