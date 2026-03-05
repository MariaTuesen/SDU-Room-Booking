package com.example.sduroombooking.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sduroombooking.R

data class BookingCardUiModel(
    val roomDbId: Int,
    val roomName: String,
    val building: String,
    val dateText: String,
    val timeText: String,
    val seatsText: String,
    val hasMonitor: Boolean,
    val hasWhiteboard: Boolean,
    val isAccessible: Boolean
)

@Composable
fun BookingCard(
    model: BookingCardUiModel,
    borderColor: Color,
    canBook: Boolean,
    onBook: () -> Unit,
    onMissingDateTime: () -> Unit
) {
    val chipScroll = rememberScrollState()

    Surface(
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(3.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Button(
                onClick = { if (canBook) onBook() else onMissingDateTime() },
                enabled = canBook,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = borderColor,
                    contentColor = Color.Black,
                    disabledContainerColor = borderColor.copy(alpha = 0.35f),
                    disabledContentColor = Color.Black.copy(alpha = 0.7f)
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .height(48.dp)
            ) {
                Text(
                    text = "Book",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black
                    )
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 100.dp)
                ) {
                    Text(
                        text = model.roomName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = model.building,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = model.dateText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                        )

                        Text(
                            text = model.timeText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    item {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    model.seatsText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }

                    if (model.hasMonitor) {
                        item {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        "TV screen",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }

                    if (model.hasWhiteboard) {
                        item {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        "Whiteboard",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }

                    if (model.isAccessible) {
                        item {
                            Icon(
                                painter = painterResource(R.drawable.wheelchair),
                                contentDescription = "Accessible",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(26.dp)
                                    .padding(start = 4.dp)
                                    .offset(y = 11.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(
    name = "Booking Card Preview",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
private fun BookingCardPreview() {
    BookingCard(
        model = BookingCardUiModel(
            roomDbId = 1,
            roomName = "ØI4-504a-3",
            building = "MMMI, SDU Odense",
            dateText = "22/2/26",
            timeText = "12-14",
            seatsText = "5 seats",
            hasMonitor = true,
            hasWhiteboard = true,
            isAccessible = true
        ),
        borderColor = Color(0xFF7A9C4E),
        canBook = true,
        onBook = {},
        onMissingDateTime = {}
    )
}