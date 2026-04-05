package com.example.sduroombooking.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.dp
import com.example.sduroombooking.R
import com.example.sduroombooking.dataclasses.NotificationItem
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.PopupGrey

@Composable
fun NotificationsPopup(
    notifications: List<NotificationItem>,
    loading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onNotificationClick: (NotificationItem) -> Unit,
    onAcceptGroupInvite: (NotificationItem) -> Unit,
    onDeclineGroupInvite: (NotificationItem) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val shape = RoundedCornerShape(16.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(min = 220.dp, max = 500.dp)
                .padding(16.dp)
                .border(2.dp, AppGreen, shape)
                .background(PopupGrey, shape)
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Notifications",
                    fontFamily = AlatsiFont,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AppGreen)
                        }
                    }

                    error != null -> {
                        Text(
                            text = error,
                            color = Color.Red,
                            fontFamily = AlatsiFont
                        )
                    }

                    notifications.isEmpty() -> {
                        Text(
                            text = "No notifications yet",
                            color = Color.DarkGray,
                            fontFamily = AlatsiFont
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(
                                notifications,
                                key = { _, item -> item.id }
                            ) { index, notification ->
                                NotificationRow(
                                    notification = notification,
                                    onClick = { onNotificationClick(notification) },
                                    onDeleteClick = {onNotificationClick(notification)},
                                    onAcceptGroupInvite = { onAcceptGroupInvite(notification) },
                                    onDeclineGroupInvite = { onDeclineGroupInvite(notification) }
                                )

                                if (index < notifications.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    notification: NotificationItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAcceptGroupInvite: () -> Unit,
    onDeclineGroupInvite: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        )
        {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            )
            {
                Text(
                    text = notification.title,
                    fontFamily = AlatsiFont,
                    color = if (notification.read) Color.Black else AppGreen
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontFamily = AlatsiFont,
                    color = Color.Black
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    painter = painterResource(R.drawable.bin),
                    contentDescription = "Delete notification",
                    tint = Color.Black,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        if (notification.type == "group_invite" && !notification.read) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.material3.Button(
                    onClick = onAcceptGroupInvite,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = AppGreen
                    )
                ) {
                    Text("Accept", color = Color.Black)
                }

                androidx.compose.material3.OutlinedButton(
                    onClick = onDeclineGroupInvite
                ) {
                    Text("Decline", color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}