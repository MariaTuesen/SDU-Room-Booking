package com.example.sduroombooking.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
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
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey
import com.example.sduroombooking.viewmodel.UserViewModel
import java.util.Locale
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars

@Composable
fun SearchPeoplePage(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var query by remember { mutableStateOf("") }
    val users = userViewModel.allUsers.value
    val loading = userViewModel.usersLoading.value
    val error = userViewModel.usersError.value
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userViewModel.fetchAllUsers()
        userViewModel.fetchFriendsFromBackend()
    }

    val currentUserId = userViewModel.currentUser.value?.id

    val filteredUsers = remember(query, users, currentUserId) {
        val q = query.trim().lowercase(Locale.getDefault())

        users
            .asSequence()
            .filter { it.id != currentUserId }
            .sortedBy { it.fullName.trim().lowercase(Locale.getDefault()) }
            .filter { user ->
                if (q.isBlank()) {
                    true
                } else {
                    user.fullName.trim().lowercase(Locale.getDefault()).startsWith(q) ||
                            user.email.trim().lowercase(Locale.getDefault()).startsWith(q)
                }
            }
            .toList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.back_button),
                    contentDescription = "Back",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                textStyle = LocalTextStyle.current.copy(fontFamily = AlatsiFont),
                placeholder = {
                    Text(
                        "Search...",
                        fontFamily = AlatsiFont,
                        color = TextFieldGrey
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextFieldGrey
                    )
                },
                singleLine = true,
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
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppGreen)
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontFamily = AlatsiFont
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 85.dp
                    )
                ) {
                    items(filteredUsers, key = { it.id }) { user ->
                        val isFriend = userViewModel.isFriend(user.id)

                        UserSearchItem(
                            user = user,
                            isFriend = isFriend,
                            onToggleFriend = {
                                userViewModel.toggleFriend(context, user)
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun UserSearchItem(
    user: User,
    isFriend: Boolean,
    onToggleFriend: () -> Unit
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
                contentDescription = "Profile picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = AlatsiFont,
                    fontSize = 18.sp
                )
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = AlatsiFont,
                    fontSize = 13.sp
                ),
                color = Color.DarkGray
            )
        }

        IconButton(onClick = onToggleFriend) {
            Icon(
                imageVector = if (isFriend) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = "Friend",
                tint = AppGreen,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}