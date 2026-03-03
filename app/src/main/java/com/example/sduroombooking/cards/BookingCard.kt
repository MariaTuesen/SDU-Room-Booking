package com.example.sduroombooking.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
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
    val roomId: String,
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
    Surface(
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(3.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {

                // Room name
                Text(
                    text = model.roomId,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.calendar),
                            contentDescription = "Calendar",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = model.dateText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.clock),
                            contentDescription = "Clock",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = model.timeText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = model.building,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AssistChip(
                        onClick = {},
                        label = { Text(model.seatsText) }
                    )

                    if (model.hasMonitor) {
                        AssistChip(
                            onClick = {},
                            label = { Text("TV screen") }
                        )
                    }

                    if (model.hasWhiteboard) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Whiteboard") }
                        )
                    }

                    if (model.isAccessible) {
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(R.drawable.wheelchair),
                            contentDescription = "Accessible",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

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
                    .height(64.dp)
                    .widthIn(min = 120.dp)
            ) {
                Text(
                    text = "Book",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black
                    )
                )
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
            roomId = "ØI4-504a-3",
            building = "MMMI, SDU Odense",
            dateText = "22/2/26",
            timeText = "12-14",
            seatsText = "5 seats",
            hasMonitor = true,
            hasWhiteboard = false,
            isAccessible = true
        ),
        borderColor = Color(0xFF7A9C4E),
        canBook = true,
        onBook = {},
        onMissingDateTime = {}
    )
}