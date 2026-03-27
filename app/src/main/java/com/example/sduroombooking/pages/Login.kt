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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextGrey
import com.example.sduroombooking.ui.theme.TextFieldGrey
import com.example.sduroombooking.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    userVM: UserViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Mail", fontFamily = AlatsiFont, color = TextGrey) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AppGreen, RoundedCornerShape(14.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppGreen,
                    unfocusedBorderColor = AppGreen,
                    focusedContainerColor = TextFieldGrey.copy(alpha=0.3f),
                    unfocusedContainerColor = TextFieldGrey.copy(alpha=0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", fontFamily = AlatsiFont, color = TextGrey) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AppGreen, RoundedCornerShape(14.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppGreen,
                    unfocusedBorderColor = AppGreen,
                    focusedContainerColor = TextFieldGrey.copy(alpha=0.3f),
                    unfocusedContainerColor = TextFieldGrey.copy(alpha=0.3f)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    userVM.login(email, password,
                        onSuccess = {
                            errorMessage = null
                            navController.navigate(Destination.CREATEBOOKING.route)
                        },
                        onError = { msg ->
                            errorMessage = msg
                        }
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                modifier = Modifier.width(140.dp).height(50.dp)
            ) {
                Text("Login", fontFamily = AlatsiFont, fontSize = 20.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text("Not a user? ", fontFamily = AlatsiFont, color = Color.Black)
                Text(
                    text = "Sign up",
                    fontFamily = AlatsiFont,
                    color = AppGreen,
                    modifier = Modifier.clickable { navController.navigate(Destination.CREATEACCOUNT.route) }
                )
            }
        }
    }
}