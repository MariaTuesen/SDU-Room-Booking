package com.example.sduroombooking.cards
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sduroombooking.R
import com.example.sduroombooking.ui.theme.AppGreen

data class BookedRoomUiModel(
    val roomName: String,
    val building: String,
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
        modifier = Modifier.fillMaxWidth()
    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
            {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Black
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end=100.dp)
                )
                {
                    Text(
                        text = model.roomName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier =Modifier.height(6.dp))

                    Text(
                        text = model.building,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier =Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically)
                    {
                        Icon(
                           painter = painterResource(R.drawable.clock),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = model.dateText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                     Icon(
                            painter = painterResource(R.drawable.calendar),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
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
         timeText = "12-14"
     ),
     onEditClick = {}
 )
}

