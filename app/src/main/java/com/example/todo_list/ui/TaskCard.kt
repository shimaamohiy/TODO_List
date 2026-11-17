package com.example.todo_list.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo_list.data.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskCard(
    task: Task,
    onCheckedChange: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val priorityColor = when (task.priority.lowercase()) {
        "high" -> Red
        "medium" -> Orange
        "low" -> Green
        else -> LightPurple
    }

    val dueDateText = task.dueDate?.let {

        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date(it))

    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppWhite
        )
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = {
                    onCheckedChange()
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = DeepPurple
                )
            )

            Spacer(Modifier.width(6.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText,
                    textDecoration =
                        if (task.isCompleted)
                            TextDecoration.LineThrough
                        else
                            TextDecoration.None
                )

                if (task.description.isNotBlank()) {

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = GrayText
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        color = LightPurple,
                        shape = RoundedCornerShape(8.dp)
                    ) {

                        Text(
                            text = task.category,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            fontSize = 10.sp,
                            color = DeepPurple
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Surface(
                        color = priorityColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {

                        Text(
                            text = task.priority,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            fontSize = 10.sp,
                            color = MainText
                        )
                    }
                }

                if (dueDateText != null) {

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = " $dueDateText",
                        fontSize = 10.sp,
                        color = GrayText
                    )
                }
            }

            IconButton(
                onClick = onDelete
            ) {

                Text(
                    text = "✕",
                    color = Color.Red,
                    fontSize = 20.sp
                )
            }
        }
    }
}