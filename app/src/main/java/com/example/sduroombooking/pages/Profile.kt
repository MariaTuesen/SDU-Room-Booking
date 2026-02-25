package com.example.sduroombooking.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sduroombooking.R
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.popup.SettingsPopup
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.viewmodel.UserViewModel

@Composable
fun Profile(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val user = userViewModel.currentUser.value
    val friends = userViewModel.friends
    val photoPicker = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && user != null) {
            userViewModel.uploadProfilePicture(context, user.id, uri)
        }
    }

    var showPopup by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.fetchAllUsers()
    }

    LaunchedEffect(user?.id) {
        if (user != null) {
            userViewModel.fetchFriendsFromBackend()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ProfileHeader(
                user = user,
                onAvatarClick = {
                    photoPicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                },
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
                        fontSize = 26.sp
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { navController.navigate(Destination.SEARCHPEOPLE.route) },
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

            FriendsBox(
                friends = friends,
                onUnfriend = { friendUser ->
                    userViewModel.toggleFriend(context, friendUser)
                },
                modifier = Modifier.height(300.dp)
            )
        }

        Button(
            onClick = {
                userViewModel.logoutClearUiOnly()
                navController.navigate(Destination.LOGIN.route) { popUpTo(0) }
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
            modifier = Modifier
                .align(Alignment.BottomCenter)
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

        if (showPopup) {
            SettingsPopup(
                onDismiss = { showPopup = false },
                onDeleteAccount = {
                    val currentUser = userViewModel.currentUser.value
                    if (currentUser == null) {
                        android.widget.Toast
                            .makeText(context, "No user logged in", android.widget.Toast.LENGTH_LONG)
                            .show()
                        return@SettingsPopup
                    }

                    userViewModel.deleteAccount(
                        context = context,
                        userId = currentUser.id,
                        onSuccess = {
                            showPopup = false
                            navController.navigate(Destination.LOGIN.route) {
                                popUpTo(0)
                            }
                        },
                        onError = { msg ->
                            android.util.Log.e("DeleteAccount", msg)
                            android.widget.Toast
                                .makeText(context, msg, android.widget.Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                },
                onTerms = {
                    showPopup = false
                    navController.navigate(Destination.TERMSANDCONDITIONS.route)
                }
            )
        }
    }
}

@Composable
fun FriendsBox(
    friends: List<User>,
    onUnfriend: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF2F2F2))
            .padding(vertical = 4.dp)
    ) {
        if (friends.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No friends yet. Tap the search icon to add some!",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = AlatsiFont),
                    color = Color.DarkGray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                items(friends, key = { it.id }) { friendUser ->
                    FriendItem(
                        user = friendUser,
                        onUnfriend = { onUnfriend(friendUser) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: User?,
    onAvatarClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val baseUrl = "http://10.0.2.2:3000"

    val imageUrl = user?.profile_picture
        ?.takeIf { it.isNotBlank() }
        ?.let { if (it.startsWith("http")) it else baseUrl + it }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { onAvatarClick() },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl ?: R.drawable.no_profile_image)
                    .crossfade(true)
                    .setParameter("ts", System.currentTimeMillis(), memoryCacheKey = null)
                    .build(),
                placeholder = painterResource(R.drawable.no_profile_image),
                error = painterResource(R.drawable.no_profile_image),
                contentDescription = "Profile",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
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

@Composable
fun FriendItem(
    user: User,
    onUnfriend: (() -> Unit)? = null
) {
    val baseUrl = "http://10.0.2.2:3000"

    val imageUrl = user.profile_picture
        ?.takeIf { it.isNotBlank() }
        ?.let { if (it.startsWith("http")) it else baseUrl + it }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl ?: R.drawable.no_profile_image)
                    .crossfade(true)
                    .setParameter("ts", System.currentTimeMillis(), memoryCacheKey = null)
                    .build(),
                placeholder = painterResource(R.drawable.no_profile_image),
                error = painterResource(R.drawable.no_profile_image),
                contentDescription = "Friend picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = AlatsiFont,
                    fontSize = 18.sp
                )
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = AlatsiFont,
                    fontSize = 13.sp
                ),
                color = Color.DarkGray
            )
        }

        IconButton(
            onClick = { onUnfriend?.invoke() },
            enabled = onUnfriend != null,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Friend",
                tint = AppGreen,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}