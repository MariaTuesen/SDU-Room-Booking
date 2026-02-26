package com.example.sduroombooking.cards

import android.text.Layout
import android.text.style.BackgroundColorSpan
import android.webkit.WebSettings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import com.example.sduroombooking.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sduroombooking.ui.theme.AppGreen
import com.example.sduroombooking.ui.theme.TextFieldGrey
import kotlinx.serialization.internal.InlinePrimitiveDescriptor


@Composable
fun BookedRoomCard()
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, AppGreen),
        colors = CardDefaults.cardColors(TextFieldGrey)
    )
    {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            //Booking information
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            )
            {
                //first section made of room number, date and time
                Row(
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "Ø14-504a-3",
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Dato",
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "22/02/26",
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )


                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.WatchLater,
                        contentDescription = "Time",
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "12-14",
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "MMMI, SDU Odense",
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

            }
            //Edit
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Time",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}





@Preview()
@Composable
fun BookedRoomCardPreview() {
    BookedRoomCard()
}