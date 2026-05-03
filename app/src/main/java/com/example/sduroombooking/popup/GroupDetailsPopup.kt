package com.example.sduroombooking.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sduroombooking.R
import com.example.sduroombooking.dataclasses.Group
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.PopupGrey
import com.example.sduroombooking.ui.theme.TextGrey

@Composable
fun GroupDetailsPopup(
    group: Group,
    allUsers: List<User>,
    currentUserId: String?,
    onDismiss: () -> Unit,
    onLeaveGroup: (Group) -> Unit,
    onInviteMembers: (Group) -> Unit
) {
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val baseUrl = "http://10.0.2.2:3000"

    val visibleParticipants = group.participants.filter { participant ->
        participant.status == "accepted" || participant.status == "pending"
    }

    val groupUsersWithStatus = visibleParticipants.mapNotNull { participant ->
        val user = allUsers.find { it.id == participant.userId }
        user?.let { it to participant.status }
    }

    val acceptedMemberCount = group.participants.count { it.status == "accepted" }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PopupGrey,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.border(
            width = 2.dp,
            color = AppGreen,
            shape = RoundedCornerShape(16.dp)
        ),
        title = {
            Text(
                text = group.name,
                fontFamily = AlatsiFont
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$acceptedMemberCount ${if (acceptedMemberCount == 1) "member" else "members"}",
                    fontFamily = AlatsiFont,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (groupUsersWithStatus.isEmpty()) {
                    Text(
                        text = "No participants found.",
                        fontFamily = AlatsiFont
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 250.dp)
                    ) {
                        items(groupUsersWithStatus, key = { it.first.id }) { (user, status) ->
                            val imageUrl = user.profile_picture
                                ?.takeIf { it.isNotBlank() }
                                ?.let { if (it.startsWith("http")) it else baseUrl + it }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(imageUrl ?: R.drawable.no_profile_image)
                                            .crossfade(true)
                                            .setParameter(
                                                "ts",
                                                System.currentTimeMillis(),
                                                memoryCacheKey = null
                                            )
                                            .build(),
                                        placeholder = painterResource(R.drawable.no_profile_image),
                                        error = painterResource(R.drawable.no_profile_image),
                                        contentDescription = "${user.fullName} profile picture",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.fullName,
                                        fontFamily = AlatsiFont
                                    )
                                    Text(
                                        text = user.email,
                                        fontFamily = AlatsiFont,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }

                                if (status == "pending") {
                                    Text(
                                        text = "Pending",
                                        fontFamily = AlatsiFont,
                                        color = Color(0xFFFF9800)
                                    )
                                }
                            }

                            HorizontalDivider()
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = { showLeaveConfirmation = true }
                ) {
                    Text(
                        text = "Leave group",
                        color = TextGrey,
                        fontFamily = AlatsiFont
                    )
                }

                Button(
                    onClick = { onInviteMembers(group) },
                    colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
                ) {
                    Text(
                        text = "Add people",
                        color = Color.Black,
                        fontFamily = AlatsiFont
                    )
                }
            }
        },
        dismissButton = {}
    )

    if (showLeaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmation = false },
            containerColor = PopupGrey,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(
                width = 2.dp,
                color = AppGreen,
                shape = RoundedCornerShape(16.dp)
            ),
            title = {
                Text(
                    text = "Leave group?",
                    fontFamily = AlatsiFont
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to leave this group?",
                    fontFamily = AlatsiFont
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLeaveConfirmation = false
                        onLeaveGroup(group)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(
                        text = "Leave",
                        color = Color.Black,
                        fontFamily = AlatsiFont
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLeaveConfirmation = false }
                ) {
                    Text(
                        text = "Cancel",
                        fontFamily = AlatsiFont,
                        color = TextGrey
                    )
                }
            }
        )
    }
}