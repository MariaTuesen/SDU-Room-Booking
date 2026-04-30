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
import androidx.compose.material.icons.filled.Group
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
import com.example.sduroombooking.dataclasses.Group
import com.example.sduroombooking.dataclasses.GroupParticipant
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.navigation.Destination
import com.example.sduroombooking.popup.DeleteAccountConfirmationPopup
import com.example.sduroombooking.popup.NotificationsPopup
import com.example.sduroombooking.popup.SettingsPopup
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey
import com.example.sduroombooking.viewmodel.GroupViewModel
import com.example.sduroombooking.viewmodel.NotificationsViewModel
import com.example.sduroombooking.viewmodel.UserViewModel
import com.example.sduroombooking.popup.CreateGroupDialog
import com.example.sduroombooking.popup.GroupDetailsPopup
import com.example.sduroombooking.popup.InviteMembersDialog

private enum class ProfileTab {
    FRIENDS, GROUPS
}

@Composable
fun Profile(
    navController: NavHostController,
    userViewModel: UserViewModel,
    notificationsViewModel: NotificationsViewModel,
    groupViewModel: GroupViewModel
) {
    val context = LocalContext.current
    val user = userViewModel.currentUser.value
    val friends = userViewModel.friends
    val hasUnreadNotifications =
        notificationsViewModel.notifications.value.any { !it.read }

    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(ProfileTab.FRIENDS) }
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var showGroupDetailsPopup by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && user != null) {
            userViewModel.uploadProfilePicture(context, user.id, uri)
        }
    }

    var showPopup by remember { mutableStateOf(false) }
    var showNotificationsPopup by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showInviteMembersPopup by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.fetchAllUsers()
    }

    LaunchedEffect(user?.id) {
        if (user != null) {
            userViewModel.fetchFriendsFromBackend()
            notificationsViewModel.fetchNotifications(user.id)
            groupViewModel.fetchGroups(user.id)
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
                hasUnreadNotifications = hasUnreadNotifications,
                onAvatarClick = {
                    photoPicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                },
                onSettingsClick = { showPopup = true },
                onNotificationsClick = {
                    showNotificationsPopup = true
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileSwitcher(
                selectedTab = selectedTab,
                onFriendsSelected = { selectedTab = ProfileTab.FRIENDS },
                onGroupsSelected = { selectedTab = ProfileTab.GROUPS },
                onSearchFriendsClick = {
                    navController.navigate(Destination.SEARCHPEOPLE.route)
                },
                onCreateGroupClick = {
                    showCreateGroupDialog = true
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                ProfileTab.FRIENDS -> {
                    FriendsBox(
                        friends = friends,
                        onUnfriend = { friendUser: User ->
                            userViewModel.toggleFriend(friendUser)
                        },
                        modifier = Modifier.height(450.dp)
                    )
                }

                ProfileTab.GROUPS -> {
                    GroupsBox(
                        groups = groupViewModel.groups,
                        onGroupClick = { group ->
                            selectedGroup = group
                            showGroupDetailsPopup = true
                        },
                        modifier = Modifier.height(450.dp)
                    )
                }
            }
        }

        if (showCreateGroupDialog && user != null) {
            CreateGroupDialog(
                friends = friends,
                onDismiss = { showCreateGroupDialog = false },
                onCreateGroup = { groupName, selectedFriendIds ->
                    groupViewModel.createGroup(
                        name = groupName,
                        creatorId = user.id,
                        invitedUserIds = selectedFriendIds,
                        onSuccess = {
                            showCreateGroupDialog = false
                            groupViewModel.fetchGroups(user.id)
                        }
                    )
                }
            )
        }

        if (showPopup) {
            SettingsPopup(
                onDismiss = { showPopup = false },
                onDeleteAccountClick = {
                    showPopup = false
                    showDeleteConfirmation = true
                },
                onTerms = {
                    showPopup = false
                    navController.navigate(Destination.TERMSANDCONDITIONS.createTermsRoute(true))
                },
                onLogoutClick = {
                    showPopup = false
                    userViewModel.logoutClearUiOnly()
                    navController.navigate(Destination.LOGIN.route) { popUpTo(0) }
                }
            )
        }

        if (showDeleteConfirmation) {
            DeleteAccountConfirmationPopup(
                userEmail = user?.email ?: "",
                onDismiss = { showDeleteConfirmation = false },
                onConfirmDelete = {
                    val currentUser = userViewModel.currentUser.value
                    if (currentUser == null) {
                        android.widget.Toast
                            .makeText(
                                context,
                                "No user logged in",
                                android.widget.Toast.LENGTH_LONG
                            )
                            .show()
                        return@DeleteAccountConfirmationPopup
                    }

                    userViewModel.deleteAccount(
                        context = context,
                        userId = currentUser.id,
                        onSuccess = {
                            showDeleteConfirmation = false
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
                }
            )
        }

        if (showNotificationsPopup) {
            NotificationsPopup(
                notifications = notificationsViewModel.notifications.value,
                loading = notificationsViewModel.notificationsLoading.value,
                error = notificationsViewModel.notificationsError.value,
                onDismiss = {
                    val currentUserId = userViewModel.currentUser.value?.id
                    if (currentUserId != null) {
                        notificationsViewModel.notifications.value
                            .filter { !it.read && it.type != "group_invite" }
                            .forEach { notification ->
                                notificationsViewModel.markNotificationAsRead(
                                    userId = currentUserId,
                                    notificationId = notification.id
                                )
                            }
                    }

                    showNotificationsPopup = false
                },
                onDeleteClick = { notification ->
                    val currentUserId = userViewModel.currentUser.value?.id
                    if (currentUserId != null) {
                        notificationsViewModel.deleteNotification(currentUserId, notification.id)
                    }
                },
                onAcceptGroupInvite = { notification ->
                    val currentUserId = userViewModel.currentUser.value?.id
                        ?: return@NotificationsPopup

                    notificationsViewModel.acceptGroupInvite(
                        userId = currentUserId,
                        notificationId = notification.id,
                        onSuccess = {
                            groupViewModel.fetchGroups(currentUserId)
                            notificationsViewModel.fetchNotifications(currentUserId)
                        }
                    )
                },
                onDeclineGroupInvite = { notification ->
                    val currentUserId = userViewModel.currentUser.value?.id
                        ?: return@NotificationsPopup

                    notificationsViewModel.declineGroupInvite(
                        userId = currentUserId,
                        notificationId = notification.id,
                        onSuccess = {
                            groupViewModel.fetchGroups(currentUserId)
                            notificationsViewModel.fetchNotifications(currentUserId)
                        }
                    )
                }
            )
        }

        if (showGroupDetailsPopup && selectedGroup != null) {
            GroupDetailsPopup(
                group = selectedGroup!!,
                allUsers = userViewModel.allUsers.value,
                currentUserId = userViewModel.currentUser.value?.id,
                onDismiss = {
                    showGroupDetailsPopup = false
                    selectedGroup = null
                },
                onLeaveGroup = { group ->
                    val currentUserId = userViewModel.currentUser.value?.id ?: return@GroupDetailsPopup

                    groupViewModel.leaveGroup(
                        groupId = group.id,
                        userId = currentUserId,
                        onSuccess = {
                            showGroupDetailsPopup = false
                            selectedGroup = null
                            groupViewModel.fetchGroups(currentUserId)
                        },
                        onError = { msg ->
                            android.widget.Toast
                                .makeText(context, msg, android.widget.Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                },
                onInviteMembers = { group ->
                    selectedGroup = group
                    userViewModel.fetchFriendsFromBackend()
                    showInviteMembersPopup = true
                }
            )
        }
        if (showInviteMembersPopup && selectedGroup != null) {
            val group = selectedGroup!!

            val activeUserIds = group.participants
                .filter { it.status == "accepted" || it.status == "pending" }
                .map { it.userId }
                .toSet()

            val inviteableUsers = userViewModel.friends.filter { user ->
                user.id !in activeUserIds
            }

            InviteMembersDialog(
                users = inviteableUsers,
                onDismiss = {
                    showInviteMembersPopup = false
                },
                onInviteUsers = { selectedUserIds ->
                    val currentUserId = userViewModel.currentUser.value?.id ?: return@InviteMembersDialog

                    groupViewModel.inviteUsersToGroup(
                        groupId = group.id,
                        invitedByUserId = currentUserId,
                        userIds = selectedUserIds,
                        onSuccess = {
                            selectedGroup = selectedGroup?.copy(
                                participants = selectedGroup!!.participants + selectedUserIds
                                    .filter { invitedUserId ->
                                        selectedGroup!!.participants.none { it.userId == invitedUserId }
                                    }
                                    .map { invitedUserId ->
                                        GroupParticipant(
                                            userId = invitedUserId,
                                            status = "pending"
                                        )
                                    }
                            )

                            groupViewModel.fetchGroups(currentUserId)
                            showInviteMembersPopup = false
                        },
                        onError = { msg ->
                            android.widget.Toast
                                .makeText(context, msg, android.widget.Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun ProfileSwitcher(
    selectedTab: ProfileTab,
    onFriendsSelected: () -> Unit,
    onGroupsSelected: () -> Unit,
    onSearchFriendsClick: () -> Unit,
    onCreateGroupClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabText(
                    text = "Your friends",
                    selected = selectedTab == ProfileTab.FRIENDS,
                    onClick = onFriendsSelected
                )

                Spacer(modifier = Modifier.width(16.dp))

                TabText(
                    text = "Groups",
                    selected = selectedTab == ProfileTab.GROUPS,
                    onClick = onGroupsSelected
                )
            }

            if (selectedTab == ProfileTab.FRIENDS) {
                IconButton(
                    onClick = onSearchFriendsClick,
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

            if (selectedTab == ProfileTab.GROUPS) {
                IconButton(
                    onClick = onCreateGroupClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.create_group),
                        contentDescription = "Create group",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(color = TextFieldGrey)
    }
}

@Composable
fun TabText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        modifier = Modifier.clickable { onClick() },
        style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = AlatsiFont,
            fontSize = 26.sp,
            color = if (selected) Color.Black else Color.Gray
        )
    )
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
            .background(TextFieldGrey.copy(alpha = 0.3f))
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
fun GroupsBox(
    groups: List<Group>,
    onGroupClick: (Group) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(TextFieldGrey.copy(alpha = 0.3f))
            .padding(vertical = 4.dp)
    ) {
        if (groups.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No groups yet.",
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
                items(groups, key = { it.id }) { group ->
                    GroupItem(
                        group = group,
                        onClick = { onGroupClick(group) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun GroupItem(
    group: Group,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(AppGreen.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = "Group",
                tint = AppGreen,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = AlatsiFont,
                    fontSize = 18.sp
                )
            )
            Text(
                text = "${group.memberCount} ${if (group.memberCount == 1) "member" else "members"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = AlatsiFont,
                    fontSize = 13.sp
                ),
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun ProfileHeader(
    user: User?,
    hasUnreadNotifications: Boolean,
    onAvatarClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(x = (-12).dp)
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.setting),
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                }

                Box {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.notification),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    }

                    if (hasUnreadNotifications) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-10).dp, y = 10.dp)
                                .background(AppGreen, CircleShape)
                        )
                    }
                }
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