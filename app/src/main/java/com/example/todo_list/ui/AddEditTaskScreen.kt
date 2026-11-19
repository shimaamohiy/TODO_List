package com.example.todo_list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo_list.data.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    task: Task? = null,
    onSave: (Task) -> Unit,
    onCancel: () -> Unit
) {

    var title by remember(task?.id) {
        mutableStateOf(task?.title ?: "")
    }

    var description by remember(task?.id) {
        mutableStateOf(task?.description ?: "")
    }

    var priority by remember(task?.id) {
        mutableStateOf(task?.priority ?: "Medium")
    }

    var category by remember(task?.id) {
        mutableStateOf(task?.category ?: "Personal")
    }

    var dueDate by remember(task?.id) {
        mutableStateOf(task?.dueDate)
    }

    var showPriorityMenu by remember {
        mutableStateOf(false)
    }

    var showCategoryMenu by remember {
        mutableStateOf(false)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate ?: System.currentTimeMillis()
    )


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 650.dp)
            .background(
                Lavender,
                RoundedCornerShape(24.dp)
            )
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
    ) {


    Text(
            text = if (task == null) "Add New Task" else "Edit Task",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = DeepPurple
        )

        Spacer(Modifier.height(20.dp))

        Text("Task Title", color = MainText)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Enter task title")
            },
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(Modifier.height(15.dp))

        Text("Description", color = MainText)

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Enter description")
            },
            shape = RoundedCornerShape(14.dp),
            minLines = 3
        )

        Spacer(Modifier.height(15.dp))

        Text("Priority", color = MainText)

        Box {

            OutlinedButton(
                onClick = {
                    showPriorityMenu = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(priority)
            }

            DropdownMenu(
                expanded = showPriorityMenu,
                onDismissRequest = {
                    showPriorityMenu = false
                }
            ) {

                listOf("Low", "Medium", "High").forEach {

                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            priority = it
                            showPriorityMenu = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(15.dp))

        Text("Category", color = MainText)

        Box {

            OutlinedButton(
                onClick = {
                    showCategoryMenu = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(category)
            }

            DropdownMenu(
                expanded = showCategoryMenu,
                onDismissRequest = {
                    showCategoryMenu = false
                }
            ) {

                listOf(
                    "Personal",
                    "Work",
                    "Study",
                    "Health",
                    "Other"
                ).forEach {

                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            category = it
                            showCategoryMenu = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(15.dp))

        Text(
            text = "Due Date",
            color = MainText
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                showDatePicker = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = dueDate?.let {
                    SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    ).format(Date(it))
                } ?: "Select Due Date"
            )
        }

        if (dueDate != null) {

            TextButton(
                onClick = {
                    dueDate = null
                }
            ) {
                Text(
                    "Remove Date",
                    color = Red
                )
            }
        }

        Spacer(Modifier.height(25.dp))

        Button(
            onClick = {

                if (title.isNotBlank()) {

                    val updatedTask = Task(
                        id = task?.id ?: 0,
                        title = title.trim(),
                        description = description.trim(),
                        priority = priority,
                        category = category,
                        dueDate = dueDate,
                        isCompleted = task?.isCompleted ?: false,
                        createdAt = task?.createdAt
                            ?: System.currentTimeMillis()
                    )

                    onSave(updatedTask)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Pink
            ),
            shape = RoundedCornerShape(15.dp)
        ) {

            Text(
                if (task == null) "Save Task" else "Update Task",
                color = MainText,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }

    if (showDatePicker) {

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let { selected ->

                            val utcCalendar = Calendar.getInstance(
                                TimeZone.getTimeZone("UTC")
                            ).apply {
                                timeInMillis = selected
                            }

                            dueDate = Calendar.getInstance().apply {
                                set(
                                    utcCalendar.get(Calendar.YEAR),
                                    utcCalendar.get(Calendar.MONTH),
                                    utcCalendar.get(Calendar.DAY_OF_MONTH),
                                    9, 0, 0
                                )
                                set(Calendar.MILLISECOND, 0)
                            }.timeInMillis
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }
}