package com.example.sduroombooking.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.viewmodel.UserViewModel
import androidx.compose.ui.res.painterResource
import com.example.sduroombooking.R

//Friends, mock data
data class Friend(
    val name: String,
    val email: String
)

val mockFriends = listOf(
    Friend("Emma Frinkley", "emfrin25@student.sdu.dk"),
    Friend("Julie Holm", "juhol25@student.sdu.dk"),
    Friend("Lucas Andersen", "luand21@student.sdu.dk"),
    Friend("Maria Tuesen", "matue23@student.sdu.dk"),
    Friend("Marie Andersen", "maand23@student.sdu.dk")
)

//Profile page
@Composable
fun Profile(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val user = userViewModel.currentUser.value

    var showPopup by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && user != null) {
            userViewModel.uploadProfilePicture(context, user.id, uri)
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            ProfileHeader(
                user = user,
                onAvatarClick = { imagePicker.launch("image/*") },
                onSettingsClick = { showPopup = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your friends",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = AlatsiFont,
                        fontSize = 26.sp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { /* TODO search friends */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search_people),
                        contentDescription = "Search friends",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(mockFriends) { friend ->
                    FriendItem(friend)
                    HorizontalDivider()
                }
            }

            Button(
                onClick = {
                    navController.navigate(Destination.LOGIN.route) {
                        popUpTo(0)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp)
                    .width(140.dp)
                    .height(50.dp)
            ) {
                Text(
                    "Logout",
                    fontFamily = AlatsiFont,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
        }

        if (showPopup) {
            SettingsPopup(
                onDismiss = { showPopup = false },
                onDeleteAccount = {
                    showPopup = false
                    // TODO delete account logic
                },
                onTerms = {
                    showPopup = false
                    // TODO open terms page
                }
            )
        }
    }
}

@Composable
fun SettingsPopup(
    onDismiss: () -> Unit,
    onDeleteAccount: () -> Unit,
    onTerms: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Button(
                    onClick = onDeleteAccount,
                    colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete account", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onTerms,
                    colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Read terms and conditions", color = Color.Black)
                }
            }
        }
    }
}

//Profile header
@Composable
fun ProfileHeader(
    user: User?,
    onAvatarClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val baseUrl = "http://10.0.2.2:3000"

    val imageUrl = user?.profile_picture?.let { path ->
        if (path.startsWith("http")) path else baseUrl + path
    }

    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { onAvatarClick() },
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .setParameter("ts", System.currentTimeMillis(), memoryCacheKey = null)
                        .build(),
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {

            val displayName = user?.fullName
                ?.trim()
                ?.split(" ")
                ?.let { parts ->
                    when {
                        parts.isEmpty() -> "Unknown user"
                        parts.size == 1 -> parts.first()
                        else -> "${parts.first()} ${parts.last()}"
                    }
                } ?: "Unknown user"

            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = AlatsiFont,
                    fontSize = 30.sp
                )
            )

            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = AlatsiFont,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            IconButton(onClick = onSettingsClick, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.setting),
                    contentDescription = "Settings",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}

//Friend item

@Composable
fun FriendItem(friend: Friend) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.name,
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = AlatsiFont)
            )

            Text(
                text = friend.email,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = AlatsiFont)
            )
        }

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Favorite",
            tint = AppGreen
        )
    }
}