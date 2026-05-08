package com.example.sduroombooking.pages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import com.example.sduroombooking.R
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.ui.theme.*
import com.example.sduroombooking.viewmodel.UserViewModel

@Composable
fun CreateAccount(
    navController: NavHostController,
    userVM: UserViewModel
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { focusManager.clearFocus() }
                )
            }
    ) {

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 140.dp)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_button),
                contentDescription = "Back",
                tint = Color.Unspecified,
                modifier = Modifier.size(50.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center)
        ) {

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = { Text("Full name", fontFamily = AlatsiFont, color = TextGrey) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AppGreen, RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text("SDU Mail", fontFamily = AlatsiFont, color = TextGrey)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AppGreen, RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text("Password", fontFamily = AlatsiFont, color = TextGrey)
                },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AppGreen, RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = {
                    Text("Confirm password", fontFamily = AlatsiFont, color = TextGrey)
                },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AppGreen, RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = acceptedTerms,
                        role = Role.Checkbox,
                        onValueChange = { acceptedTerms = it }
                    )
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(
                        checkedColor = AppGreen,
                        checkmarkColor = Color.Black,
                        uncheckedColor = AppGreen
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("I accept the ", fontFamily = AlatsiFont)

                Text(
                    text = "terms and conditions",
                    fontFamily = AlatsiFont,
                    color = AppGreen,
                    modifier = Modifier.clickable {
                        navController.navigate(
                            Destination.TERMSANDCONDITIONS.createTermsRoute(false)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                enabled = acceptedTerms,
                onClick = {
                    if (!acceptedTerms) {
                        errorMessage = "You must accept the terms and conditions"
                        return@Button
                    }
                    if (!email.endsWith("@student.sdu.dk")) {
                        errorMessage = "Only SDU student emails allowed"
                        return@Button
                    }
                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }

                    errorMessage = null

                    userVM.signup(
                        fullName,
                        email,
                        password,
                        onSuccess = {
                            navController.navigate(Destination.HOME.route)
                        },
                        onError = { msg ->
                            errorMessage = msg
                        }
                    )
                },
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

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}