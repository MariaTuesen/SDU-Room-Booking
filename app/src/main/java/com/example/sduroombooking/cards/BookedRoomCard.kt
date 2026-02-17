package com.example.sduroombooking.cards

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.sduroombooking.ui.theme.FeatureLabelGrey
import com.example.sduroombooking.ui.theme.TextFieldGrey


@Composable
fun BookedRoomCard()
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, AppGreen),
        colors = CardDefaults.cardColors(TextFieldGrey)
    )
    {
        Row()
        {
            //Booking information
            Column(
                modifier = Modifier.padding(10.dp)
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
                        painter = painterResource(id = R.drawable.calender),
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
                        painter = painterResource(id = R.drawable.time),
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

                // features
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                )
                {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp))
                    {
                        FeatureBadge("5 seats")
                        FeatureBadge("TV screen")
                        FeatureBadge("Whiteboard")
                    }
                }
            }
            //Reporting and edit
            Column()
            {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit",
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Icon(
                    painter = painterResource(id = R.drawable.report),
                    contentDescription = "Reporting",
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun FeatureBadge(text: String)
{
    Box(
        modifier = Modifier
            .background(FeatureLabelGrey, RoundedCornerShape(10.dp))
            .padding(horizontal = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}




@Preview()
@Composable
fun BookedRoomCardPreview() {
    BookedRoomCard()
}
