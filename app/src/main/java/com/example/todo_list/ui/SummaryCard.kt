package com.example.todo_list.ui

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryCard(
    title: String,
    count: Int,
    color: Color,

) {

    val animatedCount by animateIntAsState(
        targetValue = count,
        label = "Task Count"
    )

    Column(
        modifier = Modifier
            .width(105.dp)
            .height(120.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(color, RoundedCornerShape(20.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {


        Column {

            Text(
                text = animatedCount.toString(),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MainText
            )

            Text(
                text = title,
                fontSize = 15.sp,
                color = MainText
            )
        }
    }
}