package com.example.sduroombooking.pages

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sduroombooking.R
import com.example.sduroombooking.cards.BookingCard
import com.example.sduroombooking.cards.BookingCardUiModel
import com.example.sduroombooking.dataclasses.Group
import com.example.sduroombooking.dataclasses.Room
import com.example.sduroombooking.dataclasses.User
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey
import com.example.sduroombooking.viewmodel.BookingViewModel
import com.example.sduroombooking.viewmodel.GroupViewModel
import com.example.sduroombooking.viewmodel.RoomsViewModel
import com.example.sduroombooking.viewmodel.UserViewModel
import java.util.Calendar
import com.example.sduroombooking.validation.validateBookingTime
import com.example.sduroombooking.validation.validateBookingInputs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBooking(
    navController: NavController,
    userViewModel: UserViewModel,
    roomsViewModel: RoomsViewModel,
    bookingViewModel: BookingViewModel,
    groupViewModel: GroupViewModel
) {
    val outlineShape = RoundedCornerShape(12.dp)
    val context = LocalContext.current
    val bottomNavPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var selectedDate by rememberSaveable { mutableStateOf<String?>(null) }
    var startTime by rememberSaveable { mutableStateOf<String?>(null) }
    var endTime by rememberSaveable { mutableStateOf<String?>(null) }

    val canBook = selectedDate != null && startTime != null && endTime != null

    var peopleQuery by rememberSaveable { mutableStateOf("") }
    var peopleExpanded by rememberSaveable { mutableStateOf(false) }
    val selectedPeople = remember { mutableStateListOf<User>() }

    val selectedGroups = remember { mutableStateListOf<Group>() }
    var groupExpanded by rememberSaveable { mutableStateOf(false) }
    val excludedGroupMemberIds = remember { mutableStateListOf<String>() }

    val users = userViewModel.allUsers.value
    val loadingUsers = userViewModel.usersLoading.value
    val currentUserId = userViewModel.currentUser.value?.id
    val groups = groupViewModel.groups

    var filteredRooms by rememberSaveable { mutableStateOf<List<Room>>(emptyList()) }
    var searchedOnce by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.fetchAllUsers()
        userViewModel.fetchFriendsFromBackend()
        roomsViewModel.fetchRooms()
    }

    LaunchedEffect(userViewModel.currentUser.value?.id) {
        val userId = userViewModel.currentUser.value?.id ?: return@LaunchedEffect
        groupViewModel.fetchGroups(userId)
    }

    LaunchedEffect(selectedDate, startTime, endTime) {
        selectedDate?.let { bookingViewModel.fetchBookingsForDate(it) }
    }

    val friendsSnapshot = remember(userViewModel.friends) {
        userViewModel.friends.toList()
    }

    val selectedGroupMembers = remember(
        selectedGroups.toList(),
        users,
        currentUserId,
        excludedGroupMemberIds.toList()
    ) {
        val memberIds = selectedGroups
            .flatMap { it.memberIds }
            .toSet()

        users.filter { user ->
            user.id in memberIds &&
                    user.id != currentUserId &&
                    user.id !in excludedGroupMemberIds
        }
    }

    val invitedUsers = remember(selectedPeople.toList(), selectedGroupMembers) {
        (selectedPeople.toList() + selectedGroupMembers).distinctBy { it.id }
    }

    val invitedUserIds = remember(invitedUsers) {
        invitedUsers.map { it.id }
    }

    val candidatePeople by remember(
        peopleQuery, users, currentUserId, selectedPeople.toList(), selectedGroupMembers, friendsSnapshot
    ) {
        derivedStateOf {
            val qRaw = peopleQuery.trim().lowercase()
            val tokens = qRaw.split("\\s+".toRegex()).filter { it.isNotBlank() }
            val selectedIds = invitedUserIds.toSet()

            fun User.searchText(): String = "${fullName} ${email}".lowercase()

            fun User.matchesAllTokens(): Boolean {
                if (tokens.isEmpty()) return true
                val hay = searchText()
                return tokens.all { hay.contains(it) }
            }

            fun User.matchScore(): Int {
                if (tokens.isEmpty()) return 0
                val hay = searchText()
                val starts = tokens.count { hay.startsWith(it) }
                val contains = tokens.count { hay.contains(it) }
                return starts * 10 + contains
            }

            users.asSequence()
                .filter { it.id != currentUserId }
                .filter { it.id !in selectedIds }
                .filter { it.matchesAllTokens() }
                .sortedWith(
                    compareByDescending<User> { userViewModel.isFriend(it.id) }
                        .thenByDescending { it.matchScore() }
                        .thenBy { it.fullName.lowercase() }
                )
                .toList()
        }
    }

    val rooms = roomsViewModel.allRooms.value

    val locationsFromJson: List<String> = remember(rooms) {
        rooms.map { r -> r.location.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    var locationExpanded by rememberSaveable { mutableStateOf(false) }
    var location by rememberSaveable { mutableStateOf("") }

    val buildingsFromJson: List<String> = remember(rooms, location) {
        val wanted = location.trim()
        rooms.asSequence()
            .filter { r -> r.location.trim().equals(wanted, ignoreCase = true) }
            .map { it.buildingDisplay() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
            .toList()
    }

    var buildingExpanded by rememberSaveable { mutableStateOf(false) }
    var building by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(locationsFromJson) {
        if (location.isNotBlank() && location !in locationsFromJson) {
            location = ""
            building = ""
        }
    }

    LaunchedEffect(buildingsFromJson) {
        if (building.isNotBlank() && building !in buildingsFromJson) {
            building = ""
        }
    }

    data class ExtraOption(
        val key: String,
        val label: String,
        val isInRoom: (Room) -> Boolean
    )

    val possibleExtras = remember {
        listOf(
            ExtraOption("monitor", "Monitor") { it.has_monitor },
            ExtraOption("whiteboard", "Whiteboard") { it.has_whiteboard },
            ExtraOption("accessible", "Accessible") { it.is_accessible }
        )
    }

    val extrasFromJson: List<ExtraOption> = remember(rooms, possibleExtras) {
        possibleExtras.filter { opt -> rooms.any { r -> opt.isInRoom(r) } }
    }

    var selectedExtras by rememberSaveable { mutableStateOf(setOf<String>()) }

    LaunchedEffect(extrasFromJson) {
        val validKeys = extrasFromJson.map { it.key }.toSet()
        selectedExtras = selectedExtras.intersect(validKeys)
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AppGreen,
        unfocusedBorderColor = AppGreen,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        cursorColor = AppGreen
    )

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = AppGreen,
        contentColor = Color.Black,
        disabledContainerColor = TextFieldGrey,
        disabledContentColor = Color.White
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(
            top = 18.dp,
            bottom = bottomNavPadding + 85.dp
        )
    ) {
        item {
            Text(
                text = "Create booking",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "Pick date",
                        tint = AppGreen,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                val today = Calendar.getInstance()
                                val maxCalendar = Calendar.getInstance().apply {
                                    add(Calendar.MONTH, 1)
                                }

                                val dialog = DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        selectedDate = "$dayOfMonth/${month + 1}/$year"
                                    },
                                    today.get(Calendar.YEAR),
                                    today.get(Calendar.MONTH),
                                    today.get(Calendar.DAY_OF_MONTH)
                                )
                                dialog.datePicker.minDate = today.timeInMillis
                                dialog.datePicker.maxDate = maxCalendar.timeInMillis
                                dialog.show()
                            }
                    )

                    selectedDate?.let {
                        Spacer(Modifier.height(6.dp))
                        Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = "Pick start and end time",
                        tint = AppGreen,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                val cal = Calendar.getInstance()

                                TimePickerDialog(
                                    context,
                                    { _, startH, startM ->
                                        val startTotal = startH * 60 + startM

                                        TimePickerDialog(
                                            context,
                                            { _, endH, endM ->
                                                val endTotal = endH * 60 + endM
                                                val duration = endTotal - startTotal

                                                val startStr = String.format("%02d:%02d", startH, startM)
                                                val endStr = String.format("%02d:%02d", endH, endM)

                                                val error = validateBookingTime(startStr, endStr)

                                                if (error != null) {
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                                } else {
                                                    startTime = startStr
                                                    endTime = endStr
                                                }
                                            },
                                            startH,
                                            startM,
                                            true
                                        ).show()
                                    },
                                    cal.get(Calendar.HOUR_OF_DAY),
                                    cal.get(Calendar.MINUTE),
                                    true
                                ).show()
                            }
                    )

                    if (startTime != null && endTime != null) {
                        Spacer(Modifier.height(6.dp))
                        Text("$startTime - $endTime")
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = peopleExpanded,
                    onExpandedChange = { peopleExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = peopleQuery,
                        onValueChange = {
                            peopleQuery = it
                            peopleExpanded = true
                        },
                        textStyle = LocalTextStyle.current.copy(fontFamily = AlatsiFont),
                        placeholder = {
                            Text(
                                "Search for people...",
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
                            .menuAnchor()
                            .fillMaxWidth()
                            .border(2.dp, AppGreen, RoundedCornerShape(14.dp))
                            .onFocusChanged { if (it.isFocused) peopleExpanded = true },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppGreen,
                            unfocusedBorderColor = AppGreen,
                            focusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f),
                            unfocusedContainerColor = Color(0xFFD9D9D9).copy(alpha = 0.3f)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = peopleExpanded,
                        onDismissRequest = { peopleExpanded = false }
                    ) {
                        when {
                            loadingUsers -> {
                                DropdownMenuItem(
                                    text = { Text("Loading...", fontFamily = AlatsiFont) },
                                    onClick = {}
                                )
                            }

                            candidatePeople.isEmpty() -> {
                                DropdownMenuItem(
                                    text = { Text("No users found", fontFamily = AlatsiFont) },
                                    onClick = {}
                                )
                            }

                            else -> {
                                candidatePeople.forEach { user ->
                                    val isFriend = userViewModel.isFriend(user.id)
                                    DropdownMenuItem(
                                        text = { PersonDropdownRow(user = user, isFriend = isFriend) },
                                        onClick = {
                                            if (selectedPeople.none { it.id == user.id }) {
                                                selectedPeople.add(user)
                                            }
                                            excludedGroupMemberIds.remove(user.id)
                                            peopleQuery = ""
                                            peopleExpanded = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.groups),
                        contentDescription = "Select groups",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .clickable { groupExpanded = true }
                    )

                    DropdownMenu(
                        expanded = groupExpanded,
                        onDismissRequest = { groupExpanded = false }
                    ) {
                        groups.forEach { group ->
                            val isSelected = selectedGroups.any { it.id == group.id }

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = group.name,
                                            fontFamily = AlatsiFont
                                        )

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Selected",
                                                tint = AppGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    if (isSelected) {
                                        selectedGroups.removeAll { it.id == group.id }
                                    } else {
                                        selectedGroups.add(group)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(invitedUsers, key = { it.id }) { person ->
                    BookingPersonItemUser(
                        user = person,
                        onRemove = {
                            selectedPeople.removeAll { it.id == person.id }
                            if (selectedGroups.any { group -> person.id in group.memberIds }) {
                                if (person.id !in excludedGroupMemberIds) {
                                    excludedGroupMemberIds.add(person.id)
                                }
                            }
                        },
                        removable = true
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Location",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    "Building",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { locationExpanded = !locationExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = {},
                        readOnly = true,
                        enabled = locationsFromJson.isNotEmpty(),
                        placeholder = { Text("Select location") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded)
                        },
                        shape = outlineShape,
                        colors = textFieldColors,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .border(2.dp, AppGreen, outlineShape)
                    )

                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false }
                    ) {
                        locationsFromJson.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    location = option
                                    building = ""
                                    locationExpanded = false
                                }
                            )
                        }
                    }
                }

                val buildingEnabled = location.isNotBlank() && buildingsFromJson.isNotEmpty()

                ExposedDropdownMenuBox(
                    expanded = buildingExpanded,
                    onExpandedChange = {
                        if (buildingEnabled) buildingExpanded = !buildingExpanded
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = building,
                        onValueChange = {},
                        readOnly = true,
                        enabled = buildingEnabled,
                        placeholder = { Text("Select building") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = buildingExpanded)
                        },
                        shape = outlineShape,
                        colors = textFieldColors,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .border(2.dp, AppGreen, outlineShape)
                    )

                    ExposedDropdownMenu(
                        expanded = buildingExpanded,
                        onDismissRequest = { buildingExpanded = false }
                    ) {
                        buildingsFromJson.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    building = option
                                    buildingExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            if (extrasFromJson.isNotEmpty()) {
                Text(
                    "Extra",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
            }
        }

        item {
            if (extrasFromJson.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    extrasFromJson.forEach { extra ->
                        BookingExtraChip(
                            text = extra.label,
                            selected = extra.key in selectedExtras,
                            onClick = {
                                selectedExtras =
                                    if (extra.key in selectedExtras) selectedExtras - extra.key
                                    else selectedExtras + extra.key
                            }
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        val error = validateBookingInputs(
                            selectedDate,
                            startTime,
                            endTime,
                            location,
                            building
                        )

                        if (error != null) {
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val all = roomsViewModel.allRooms.value
                        val requiredSeats = 1 + invitedUserIds.size
                        val wantedLocation = location.trim()
                        val wantedBuilding = building.trim()
                        val selectedExtraOptions = extrasFromJson.filter { it.key in selectedExtras }

                        filteredRooms = all.filter { r ->
                            val locationOk = r.location.trim().equals(wantedLocation, ignoreCase = true)
                            val buildingOk = r.buildingDisplay().equals(wantedBuilding, ignoreCase = true)
                            val seatsOk = r.seats >= requiredSeats
                            val extrasOk = selectedExtraOptions.all { opt -> opt.isInRoom(r) }

                            locationOk && buildingOk && seatsOk && extrasOk
                        }

                        searchedOnce = true

                        if (filteredRooms.isEmpty()) {
                            Toast.makeText(
                                context,
                                "No rooms match your filters",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = buttonColors,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(44.dp),
                    enabled = location.isNotBlank() && building.isNotBlank() && canBook
                ) {
                    Text(
                        "Find room",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        if (searchedOnce) {
            val bookingsToday = bookingViewModel.bookingsForSelectedDate.value

            val availableRooms = filteredRooms.filter { room ->
                if (selectedDate == null || startTime == null || endTime == null) {
                    true
                } else {
                    bookingsToday.none { b ->
                        b.roomId == room.id &&
                                b.date == selectedDate &&
                                overlaps(startTime!!, endTime!!, b.startTime, b.endTime)
                    }
                }
            }

            if (availableRooms.isEmpty()) {
                item {
                    Text(
                        text = "No available rooms for that time",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            } else {
                items(
                    availableRooms,
                    key = { room -> room.id }
                ) { room ->
                    val model = BookingCardUiModel(
                        roomDbId = room.id,
                        roomName = room.name,
                        building = "${room.buildingDisplay()}, ${room.location.trim()}",
                        dateText = selectedDate ?: "",
                        timeText = "${startTime ?: ""}-${endTime ?: ""}",
                        seatsText = "${room.seats} seats",
                        hasMonitor = room.has_monitor,
                        hasWhiteboard = room.has_whiteboard,
                        isAccessible = room.is_accessible
                    )

                    BookingCard(
                        model = model,
                        borderColor = AppGreen,
                        canBook = canBook,
                        onBook = {
                            val bookingUserId = userViewModel.currentUser.value?.id
                            if (bookingUserId != null) {
                                bookingViewModel.createBooking(
                                    currentUserId = bookingUserId,
                                    roomId = room.id,
                                    date = selectedDate!!,
                                    startTime = startTime!!,
                                    endTime = endTime!!,
                                    selectedOtherUserIds = invitedUserIds,
                                    onSuccess = {
                                        Toast.makeText(context, "Booked!", Toast.LENGTH_SHORT).show()
                                        bookingViewModel.fetchBookingsForDate(selectedDate!!)
                                    },
                                    onError = { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        bookingViewModel.fetchBookingsForDate(selectedDate!!)
                                    }
                                )
                            }
                        },
                        onMissingDateTime = {
                            Toast.makeText(
                                context,
                                "Please select date and time first",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingExtraChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) AppGreen else Color.Transparent,
        contentColor = if (selected) Color.Black else AppGreen,
        border = BorderStroke(2.5.dp, AppGreen)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = AlatsiFont,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun PersonDropdownRow(
    user: User,
    isFriend: Boolean
) {
    val baseUrl = "http://10.0.2.2:3000"
    val imageUrl = user.profile_picture
        ?.takeIf { it.isNotBlank() }
        ?.let { if (it.startsWith("http")) it else baseUrl + it }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl ?: R.drawable.no_profile_image)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.no_profile_image),
                error = painterResource(R.drawable.no_profile_image),
                contentDescription = "Profile picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleSmall.copy(fontFamily = AlatsiFont),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = AlatsiFont),
                color = Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = if (isFriend) Icons.Default.Star else Icons.Outlined.StarOutline,
            contentDescription = "Friend",
            tint = AppGreen,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun BookingPersonItemUser(
    user: User,
    onRemove: () -> Unit,
    removable: Boolean = true
) {
    val baseUrl = "http://10.0.2.2:3000"
    val imageUrl = user.profile_picture
        ?.takeIf { it.isNotBlank() }
        ?.let { if (it.startsWith("http")) it else baseUrl + it }

    Row(
        modifier = Modifier
            .width(220.dp)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl ?: R.drawable.no_profile_image)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.no_profile_image),
                error = painterResource(R.drawable.no_profile_image),
                contentDescription = "Profile picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleSmall.copy(fontFamily = AlatsiFont),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = AlatsiFont),
                color = Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (removable) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.bin),
                    contentDescription = "Remove",
                    tint = AppGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun Room.buildingDisplay(): String = this.building.trim()

private fun timeToMinutes(hhmm: String): Int {
    val parts = hhmm.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return h * 60 + m
}

private fun overlaps(startA: String, endA: String, startB: String, endB: String): Boolean {
    val a1 = timeToMinutes(startA)
    val a2 = timeToMinutes(endA)
    val b1 = timeToMinutes(startB)
    val b2 = timeToMinutes(endB)
    return a1 < b2 && a2 > b1
}