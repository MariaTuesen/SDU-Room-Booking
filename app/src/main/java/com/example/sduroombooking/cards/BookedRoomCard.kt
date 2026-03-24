package com.example.sduroombooking.cards
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sduroombooking.ui.theme.AppGreen

data class BookedRoomUiModel(
    val roomName: String,
    val building: String,
    val date: String,
    val timeRange: String,
    val roomDbId: Int,
    val dateText: String,
    val timeText: String
)
@Composable
fun BookedRoomCard(
    model: BookedRoomUiModel,
    onEditClick: () -> Unit
)
{
    val borderColor = AppGreen

    Surface(
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        {
            Column(modifier = Modifier.fillMaxWidth())
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                )
                {
                    Text(
                        text = model.roomName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = model.dateText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically)
                    {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = model.timeText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = model.building,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = Color.Black,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
            {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
            }
        }
    }
}


@Composable
@Preview
    (
            name = "Booked Room Card Preview",
            showBackground = true,
            backgroundColor = 0xFFF5F5F5
            )
private fun BookedRoomCardPreview()
{
 BookedRoomCard(
     model = BookedRoomUiModel(
         roomDbId = 1,
         roomName = "ØI4-504a-3",
         building = "MMMI, SDU Odense",
         dateText = "22/2/26",
         timeText = "12-14",
         date ="22/2/26",
         timeRange = "12-14"
     ),
     onEditClick = {}
 )
}

