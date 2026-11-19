package com.example.todo_list.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.text.style.TextOverflow
@Composable
fun TaskCard(
    task: Task,
    onCheckedChange: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val currentLocale = configuration.locales[0]

    val priorityColor = when (task.priority.lowercase()) {
        "high" -> Red
        "medium" -> Orange
        "low" -> Green
        else -> LightPurple
    }

    val dueDateText = task.dueDate?.let {
        SimpleDateFormat(
            "dd MMM yyyy",
            currentLocale
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
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
                        fontSize = 13.sp,
                        color = GrayText,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }


                    Spacer(Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
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

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = onShare
                    ) {
                        Text(
                            text = "⤴",
                            color = DeepPurple,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDelete
                    ) {
                        Text(
                            text = "✕",
                            color = Color.Red,
                            fontSize = 19.sp
                        )
                    }
                }
            }
        }
    }
