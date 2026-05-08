package com.example.sduroombooking.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sduroombooking.R
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.viewmodel.UserViewModel

@Composable
fun TermsAndConditions(
    navController: NavController,
    userViewModel: UserViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 80.dp
            )
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = buildAnnotatedString {

                append("Last updated: May 2026\n\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("1. Acceptance of Terms\n")
                }
                append("By using the SDU Room Booking application, you agree to these Terms and Conditions.\n\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("2. Eligibility\n")
                }
                append("This application is intended solely for students at the University of Southern Denmark (SDU). By creating an account, you confirm that you are a student at SDU.\n\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("3. User Accounts\n")
                }
                append("• You are responsible for keeping your login credentials secure.\n")
                append("• You must not share your account with others or use another person's account.\n")
                append("• You are responsible for all activity that occurs under your account.\n\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("4. Acceptable Use\n")
                }
                append("You agree to use the application only for its intended purpose of booking study rooms at SDU. You must not:\n")
                append("• Create false or misleading bookings\n")
                append("• Book rooms on behalf of others without their knowledge\n")
                append("• Attempt to manipulate or abuse the booking system\n\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("5. Data Storage and Privacy\n")
                }
                append("This application is currently a prototype. All user data, including account information, bookings, and group memberships, is stored in JSON files on a backend server. As a result:\n")
                append("• Data is not encrypted at a production level\n")
                append("• The application does not guarantee data security or persistence\n")
                append("• You should not store sensitive personal information beyond what is required to use the app\n\n")
                append("By using this application you acknowledge and accept these limitations.\n\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("6. Bookings\n")
                }
                append("• Bookings are subject to room availability at the time of creation.\n")
                append("• You are expected to honour your bookings and cancel them promptly if your plans change, so that rooms remain available to other students.\n")
                append("• The application does not guarantee that a room will be available or accessible at the time of your booking.\n\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("7. Groups and Social Features\n")
                }
                append("When using group features, you agree to only invite people you know and who have consented to being added. You must not use the group feature to harass other users or add users without their consent.\n\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("8. Disclaimer\n")
                }
                append("This application is developed as a student project and is provided \"as is\", without any warranty. The developers and SDU are not liable for any inconvenience, data loss, or issues arising from the use of this application.\n\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("9. Changes to These Terms\n")
                }
                append("These terms may be updated at any time. Continued use of the application following any changes constitutes acceptance of the new terms.\n\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("10. Contact\n")
                }
                append("If you have any questions or concerns regarding these terms, please contact the development team at:\n\n")
                append("matue23@student.sdu.dk\n")
                append("sofim23@student.sdu.dk")
            },
            style = TextStyle(fontFamily = AlatsiFont)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}