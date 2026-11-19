
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
import androidx.compose.ui.text.style.TextOverflow
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppWhite)
            .navigationBarsPadding()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach { item ->

                val selected = selectedItem == item.first

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 56.dp)
                        .background(
                            color = if (selected) LightPink else AppWhite,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable {
                            onItemSelected(item.first)
                        }
                        .padding(
                            horizontal = 2.dp,
                            vertical = 6.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = item.second,
                        fontSize = 22.sp,
                        color = if (selected) DeepPurple else GrayText
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.first,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
}
