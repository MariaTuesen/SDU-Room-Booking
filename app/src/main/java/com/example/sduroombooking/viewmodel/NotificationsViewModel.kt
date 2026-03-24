package com.example.sduroombooking.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sduroombooking.apisetup.RetrofitClient
import com.example.sduroombooking.dataclasses.NotificationItem
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    var notifications = mutableStateOf<List<NotificationItem>>(emptyList())
    var notificationsLoading = mutableStateOf(false)
    var notificationsError = mutableStateOf<String?>(null)

    fun fetchNotifications(userId: String) {
        viewModelScope.launch {
            notificationsLoading.value = true
            notificationsError.value = null
            try {
                notifications.value = RetrofitClient.api.getNotifications(userId)
            } catch (e: Exception) {
                notificationsError.value = "Failed to fetch notifications: ${e.message}"
                notifications.value = emptyList()
            } finally {
                notificationsLoading.value = false
            }
        }
    }

    fun markNotificationAsRead(
        userId: String,
        notificationId: String
    ) {
        viewModelScope.launch {
            try {
                RetrofitClient.api.markNotificationAsRead(userId, notificationId)
                notifications.value = notifications.value.map {
                    if (it.id == notificationId) it.copy(read = true) else it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val unreadNotificationCount: Int
        get() = notifications.value.count { !it.read }
}