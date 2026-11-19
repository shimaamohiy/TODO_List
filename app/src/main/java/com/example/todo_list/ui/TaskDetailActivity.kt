package com.example.todo_list.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo_list.data.TaskDatabase
import com.example.todo_list.data.TaskRepository
import com.example.todo_list.utils.IntentHelper
import com.example.todo_list.viewmodel.TaskViewModel
import com.example.todo_list.viewmodel.TaskViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.foundation.layout.FlowRow



class TaskDetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)

        val database = TaskDatabase.getDatabase(applicationContext)
        val repository = TaskRepository(database.taskDao())
        val factory = TaskViewModelFactory(repository)

        setContent {
            val taskViewModel: TaskViewModel = viewModel(factory = factory)

            TaskDetailScreen(
                taskId = taskId,
                viewModel = taskViewModel,
                onBack = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val currentLocale = configuration.locales[0]
    val taskState by viewModel.getTaskById(taskId).collectAsState(initial = null)
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Lavender,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Task Details",
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text(
                            "←",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepPurple
                        )
                    }
                },
                actions = {
                    taskState?.let { currentTask ->
                        // Implicit Intent shortcut in toolbar
                        IconButton(onClick = { IntentHelper.shareTask(context, currentTask) }) {
                            Text("⤴", fontSize = 20.sp, color = DeepPurple)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Lavender
                )
            )
        }
    ) { padding ->
        val currentTask = taskState

        if (currentTask == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Task not found",
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Pink)
                    ) {
                        Text("Go Back", color = MainText)
                    }
                }
            }
        } else {
            val priorityColor = when (currentTask.priority.lowercase()) {
                "high" -> Red
                "medium" -> Orange
                "low" -> Green
                else -> LightPurple
            }

            val formattedDueDate = currentTask.dueDate?.let {
                SimpleDateFormat("EEE, dd MMM yyyy", currentLocale).format(Date(it))
            }

            val formattedCreatedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", currentLocale)
                .format(Date(currentTask.createdAt))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(8.dp))



                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Surface(
                        color = if (currentTask.isCompleted) Green.copy(alpha = 0.2f) else Orange.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentTask.isCompleted) "✓ Completed" else "⏳ Pending",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (currentTask.isCompleted) Green else Orange
                        )
                    }
                        Surface(
                            color = LightPurple,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = currentTask.category,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = DeepPurple
                            )
                        }

                        Surface(
                            color = priorityColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${currentTask.priority} Priority",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MainText
                            )
                        }
                }

                Spacer(Modifier.height(16.dp))


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppWhite)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = currentTask.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainText
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Description",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GrayText
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (currentTask.description.isNotBlank()) currentTask.description else "No description provided.",
                            fontSize = 15.sp,
                            color = if (currentTask.description.isNotBlank()) MainText else GrayText,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = LightGray)
                        Spacer(Modifier.height(12.dp))



                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                        Column {
                                Text("Due Date", fontSize = 12.sp, color = GrayText)
                                Text(
                                    formattedDueDate ?: "None",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (formattedDueDate != null) DeepPurple else GrayText
                                )
                            }
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Created At", fontSize = 12.sp, color = GrayText)
                                Text(
                                    formattedCreatedDate,
                                    fontSize = 12.sp,
                                    color = GrayText
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))


                Text(
                    text = "Quick Actions",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { IntentHelper.shareTask(context, currentTask) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPurple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("⤴  Share Task", color = DeepPurple, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = { IntentHelper.searchTaskOnline(context, currentTask.title) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPink),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Search on Web", color = DeepPurple, fontWeight = FontWeight.Bold)
                }


                if (currentTask.dueDate != null) {
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { IntentHelper.addEventToCalendar(context, currentTask) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8E0FF)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Add to Calendar", color = DeepPurple, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))


                Text(
                    text = "Manage Task",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )

                Spacer(Modifier.height(12.dp))


                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                Button(
                        onClick = { viewModel.toggleCompleted(currentTask) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentTask.isCompleted) Orange.copy(alpha = 0.8f) else Green.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            if (currentTask.isCompleted) "Mark Pending" else "Mark Done",
                            color = AppWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Edit Task", color = DeepPurple, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Red),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Delete Task", color = Red, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }



    if (showEditDialog && taskState != null) {
        Dialog(
            onDismissRequest = {
                showEditDialog = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .imePadding(),
                contentAlignment = Alignment.Center
            ) {
                val dialogWidth = minOf(maxWidth * 0.92f, 560.dp)
                val dialogHeight = minOf(maxHeight * 0.90f, 650.dp)

                Surface(
                    modifier = Modifier
                        .width(dialogWidth)
                        .heightIn(max = dialogHeight),
                    shape = RoundedCornerShape(24.dp),
                    color = Lavender
                ) {
                    AddEditTaskScreen(
                        task = taskState,
                        onSave = { updated ->
                            viewModel.updateTask(updated)
                            showEditDialog = false
                        },
                        onCancel = {
                            showEditDialog = false
                        }
                    )
                }
            }
        }
    }



    if (showDeleteConfirmDialog && taskState != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete '${taskState?.title}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskState?.let {
                            viewModel.deleteTask(it)
                            showDeleteConfirmDialog = false
                            onBack()
                        }
                    }
                ) {
                    Text("Delete", color = Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = MainText)
                }
            }
        )
    }
}
