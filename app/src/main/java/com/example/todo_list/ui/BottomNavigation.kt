package com.example.todo_list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigation(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {

    val items = listOf(
        "Home" to "⌂",
        "Tasks" to "☷",
        "Completed" to "✓"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppWhite)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        items.forEach { item ->

            val selected = selectedItem == item.first

            Column(
                modifier = Modifier
                    .clickable {
                        onItemSelected(item.first)
                    }
                    .background(
                        if (selected) LightPink
                        else AppWhite,
                        RoundedCornerShape(15.dp)
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 7.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = item.second,
                    fontSize = 22.sp,
                    color = if (selected) DeepPurple else GrayText
                )

                Text(
                    text = item.first,
                    fontSize = 11.sp,
                    fontWeight = if (selected)
                        FontWeight.Bold
                    else
                        FontWeight.Normal,
                    color = if (selected) DeepPurple else GrayText
                )
            }
        }
    }
}