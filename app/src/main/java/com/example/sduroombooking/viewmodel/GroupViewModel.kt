package com.example.sduroombooking.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sduroombooking.apisetup.RetrofitClient
import com.example.sduroombooking.dataclasses.CreateGroupRequest
import com.example.sduroombooking.dataclasses.Group
import com.example.sduroombooking.dataclasses.GroupInviteRequest
import com.example.sduroombooking.dataclasses.InviteUsersToGroupRequest
import kotlinx.coroutines.launch

class GroupViewModel : ViewModel() {

    private val _groups = mutableStateListOf<Group>()
    val groups: List<Group> get() = _groups

    var groupsLoading = mutableStateOf(false)
    var groupsError = mutableStateOf<String?>(null)
    var selectedGroup = mutableStateOf<Group?>(null)
    var selectedGroupLoading = mutableStateOf(false)
    var selectedGroupError = mutableStateOf<String?>(null)

    fun fetchGroups(userId: String) {
        viewModelScope.launch {
            groupsLoading.value = true
            groupsError.value = null
            try {
                val result = RetrofitClient.api.getGroups(userId)
                _groups.clear()
                _groups.addAll(result)
            } catch (e: Exception) {
                groupsError.value = e.message
                _groups.clear()
            } finally {
                groupsLoading.value = false
            }
        }
    }

    fun createGroup(
        name: String,
        creatorId: String,
        invitedUserIds: List<String>,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                RetrofitClient.api.createGroup(
                    CreateGroupRequest(
                        name = name,
                        creatorId = creatorId,
                        invitedUserIds = invitedUserIds
                    )
                )
                fetchGroups(creatorId)
                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e.message ?: "Failed to create group")
            }
        }
    }

    fun fetchGroupById(groupId: String) {
        viewModelScope.launch {
            selectedGroupLoading.value = true
            selectedGroupError.value = null
            try {
                selectedGroup.value = RetrofitClient.api.getGroupById(groupId)
            } catch (e: Exception) {
                selectedGroupError.value = e.message ?: "Failed to fetch group"
                selectedGroup.value = null
            } finally {
                selectedGroupLoading.value = false
            }
        }
    }

    fun leaveGroup(
        groupId: String,
        userId: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.leaveGroup(groupId, userId)
                if (response.isSuccessful) {
                    _groups.removeAll { it.id == groupId }
                    onSuccess?.invoke()
                } else {
                    onError?.invoke("Failed to leave group (HTTP ${response.code()})")
                }
            } catch (e: Exception) {
                onError?.invoke(e.message ?: "Failed to leave group")
            }
        }
    }

    fun inviteUsersToGroup(
        groupId: String,
        invitedByUserId: String,
        userIds: List<String>,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                userIds.forEach { userId ->
                    val response = RetrofitClient.api.inviteToGroup(
                        groupId = groupId,
                        req = GroupInviteRequest(
                            groupId = groupId,
                            invitedUserId = userId,
                            invitedByUserId = invitedByUserId
                        )
                    )

                    if (!response.isSuccessful) {
                        onError("Failed to invite user $userId (HTTP ${response.code()})")
                        return@launch
                    }
                }

                onSuccess()

            } catch (e: Exception) {
                onError(e.message ?: "Failed to invite users")
            }
        }
    }
}