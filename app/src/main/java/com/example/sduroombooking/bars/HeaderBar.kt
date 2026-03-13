package com.example.sduroombooking.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sduroombooking.ui.theme.AlatsiFont
import com.example.sduroombooking.ui.theme.AppGreen

@Composable
fun HeaderBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            )
            .background(AppGreen)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(80.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "SDU Booking",
                fontSize = 35.sp,
                fontFamily = AlatsiFont,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderBarPreview() {
    HeaderBar()
}