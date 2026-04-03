package com.example.sduroombooking.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.PopupGrey
import com.example.sduroombooking.ui.theme.TextGrey

@Composable
fun CreateGroupDialog(
    friends: List<User>,
    onDismiss: () -> Unit,
    onCreateGroup: (String, List<String>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var showNameError by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<String>() }
    val context = LocalContext.current
    val baseUrl = "http://10.0.2.2:3000"

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
                text = "Create new group",
                fontFamily = AlatsiFont
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = {
                        groupName = it
                        if (it.trim().isNotEmpty()) {
                            showNameError = false
                        }
                    },
                    label = {
                        Text(
                            text = "Group name",
                            fontFamily = AlatsiFont
                        )
                    },
                    singleLine = true,
                    isError = showNameError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppGreen,
                        unfocusedBorderColor = TextGrey,
                        focusedLabelColor = AppGreen,
                        unfocusedLabelColor = TextGrey,
                        cursorColor = AppGreen
                    )
                )

                if (showNameError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Please enter a group title",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontFamily = AlatsiFont
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Invite friends",
                    fontFamily = AlatsiFont
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(friends, key = { it.id }) { friend ->
                        val imageUrl = friend.profile_picture
                            ?.takeIf { it.isNotBlank() }
                            ?.let { if (it.startsWith("http")) it else baseUrl + it }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (selectedIds.contains(friend.id)) {
                                        selectedIds.remove(friend.id)
                                    } else {
                                        selectedIds.add(friend.id)
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedIds.contains(friend.id),
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        if (!selectedIds.contains(friend.id)) {
                                            selectedIds.add(friend.id)
                                        }
                                    } else {
                                        selectedIds.remove(friend.id)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = AppGreen,
                                    uncheckedColor = AppGreen,
                                    checkmarkColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

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
                                    contentDescription = "${friend.fullName} profile picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = friend.fullName,
                                    fontFamily = AlatsiFont
                                )
                                Text(
                                    text = friend.email,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontFamily = AlatsiFont
                                )
                            }
                        }

                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = groupName.trim()
                    if (trimmedName.isEmpty()) {
                        showNameError = true
                    } else {
                        onCreateGroup(trimmedName, selectedIds.toList())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
            ) {
                Text(
                    text = "Create",
                    color = Color.White,
                    fontFamily = AlatsiFont
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = TextGrey,
                    fontFamily = AlatsiFont
                )
            }
        }
    )
}