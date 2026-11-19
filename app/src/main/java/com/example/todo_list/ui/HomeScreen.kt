package com.example.todo_list.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.todo_list.data.Task
import com.example.todo_list.notifications.NotificationHelper
import com.example.todo_list.utils.IntentHelper
import com.example.todo_list.viewmodel.TaskViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    initialSharedText: String? = null
) {
    val context = LocalContext.current

    val visibleTasks by viewModel.filteredTasks.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedPriority by viewModel.selectedPriority.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var selectedItem by remember { mutableStateOf("Home") }
    var showAddEditScreen by remember { mutableStateOf(initialSharedText != null) }
    var selectedTask by remember {
        mutableStateOf<Task?>(
            if (initialSharedText != null) Task(title = "", description = initialSharedText) else null
        )
    }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    val (todayStart, tomorrowStart) = remember {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val tomorrow = Calendar.getInstance().apply {
            timeInMillis = today
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        today to tomorrow
    }

    val tasksToDisplay = remember(visibleTasks, selectedItem, todayStart, tomorrowStart) {
        when (selectedItem) {
            "Completed" -> visibleTasks.filter { it.isCompleted }
            "Tasks" -> visibleTasks
            else -> visibleTasks.filter { task ->
                !task.isCompleted &&
                        task.dueDate != null &&
                        task.dueDate >= todayStart &&
                        task.dueDate < tomorrowStart
            }
        }
    }

    val categories = remember {
        listOf("All", "Personal", "Work", "Study", "Health", "Other")
    }

    val priorities = remember {
        listOf("All", "High", "Medium", "Low")
    }

    Scaffold(
        containerColor = Lavender,
        bottomBar = {
            BottomNavigation(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedTask = null
                    showAddEditScreen = true
                },
                containerColor = Pink,
                contentColor = MainText,
                shape = CircleShape
            ) {
                Text("+", fontSize = 28.sp)
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 14.dp,
                bottom = 88.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(key = "header_overview") {
                Column {
                    Text(
                        "Hello, Welcome! ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple
                    )
                    Text(
                        "Let's organize your day.",
                        fontSize = 13.sp,
                        color = GrayText
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        "Your Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )

                    Spacer(Modifier.height(8.dp))

                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val isNarrowScreen = maxWidth < 340.dp
                        if (isNarrowScreen) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SummaryCard(
                                    title = "Total Tasks",
                                    count = totalCount,
                                    color = LightPurple,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                SummaryCard(
                                    title = "Completed",
                                    count = completedCount,
                                    color = LightPink,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                SummaryCard(
                                    title = "Pending",
                                    count = totalCount - completedCount,
                                    color = Color(0xFFE8E0FF),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                SummaryCard(
                                    title = "Total Tasks",
                                    count = totalCount,
                                    color = LightPurple,
                                    modifier = Modifier.weight(1f)
                                )
                                SummaryCard(
                                    title = "Completed",
                                    count = completedCount,
                                    color = LightPink,
                                    modifier = Modifier.weight(1f)
                                )
                                SummaryCard(
                                    title = "Pending",
                                    count = totalCount - completedCount,
                                    color = Color(0xFFE8E0FF),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            item(key = "progress_card") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppWhite, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Your Progress",
                            fontWeight = FontWeight.Bold,
                            color = MainText
                        )
                        Text(
                            "${(progress * 100).toInt()}%",
                            fontWeight = FontWeight.Bold,
                            color = DeepPurple
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Red,
                        trackColor =Pink ,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }

            item(key = "search_and_filters") {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearch(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search tasks...") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearch("") }) {
                                    Text("✕")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepPurple,
                            unfocusedBorderColor = LightPurple,
                            focusedContainerColor = AppWhite,
                            unfocusedContainerColor = AppWhite
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Priority",
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        priorities.forEach { priority ->
                            FilterChip(
                                selected = selectedPriority == priority,
                                onClick = { viewModel.updatePriority(priority) },
                                label = { Text(priority) },
                                modifier = Modifier.padding(end = 7.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LightPurple,
                                    selectedLabelColor = DeepPurple
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "Category",
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { viewModel.updateCategory(category) },
                                label = { Text(category) },
                                modifier = Modifier.padding(end = 7.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LightPink,
                                    selectedLabelColor = DeepPurple
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        when (selectedItem) {
                            "Completed" -> "Completed Tasks"
                            "Tasks" -> "All Tasks"
                            else -> "Today's Tasks"
                        },
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )
                }
            }

            if (tasksToDisplay.isEmpty()) {
                item(key = "empty_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val hasFilters = searchQuery.isNotEmpty() ||
                                    selectedPriority != "All" ||
                                    selectedCategory != "All"

                            Text(
                                if (hasFilters) "No matching tasks found" else "No tasks yet!",
                                fontWeight = FontWeight.Bold,
                                color = DeepPurple,
                                fontSize = 17.sp
                            )
                            Text(
                                if (hasFilters) "Try changing your search or filters."
                                else "Add your first task and get organized ",
                                color = GrayText,
                                fontSize = 13.sp
                            )
                            if (hasFilters) {
                                TextButton(onClick = { viewModel.clearFilters() }) {
                                    Text("Clear Filters", color = DeepPurple)
                                }
                            }
                        }
                    }
                }
            } else {
                items(
                    items = tasksToDisplay,
                    key = { it.id }
                ) { task ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TaskCard(
                            task = task,
                            onCheckedChange = {
                                val willBeCompleted = !task.isCompleted
                                viewModel.toggleCompleted(task)
                                if (willBeCompleted) {
                                    NotificationHelper.cancelReminder(context, task.id)
                                } else if (task.dueDate != null) {
                                    NotificationHelper.scheduleReminder(
                                        context,
                                        task.copy(isCompleted = false)
                                    )
                                }
                            },
                            onClick = { IntentHelper.openTaskDetail(context, task.id) },
                            onShare = { IntentHelper.shareTask(context, task) },
                            onDelete = { taskToDelete = task }
                        )
                    }
                }
            }
        }
    }

    if (showAddEditScreen) {
        Dialog(
            onDismissRequest = { showAddEditScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
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
                        task = selectedTask,
                        onSave = { savedTask ->
                            if (selectedTask == null) {
                                viewModel.insertTask(savedTask) { insertedTask ->
                                    if (!insertedTask.isCompleted && insertedTask.dueDate != null) {
                                        NotificationHelper.scheduleReminder(context, insertedTask)
                                    }
                                }
                            } else {
                                viewModel.updateTask(savedTask)
                                if (savedTask.isCompleted || savedTask.dueDate == null) {
                                    NotificationHelper.cancelReminder(context, savedTask.id)
                                } else {
                                    NotificationHelper.scheduleReminder(context, savedTask)
                                }
                            }
                            showAddEditScreen = false
                        },
                        onCancel = { showAddEditScreen = false }
                    )
                }
            }
        }
    }

    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task?") },
            text = {
                Text(
                    "Are you sure you want to delete this task? " +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDelete?.let { task ->
                            NotificationHelper.cancelReminder(context, task.id)
                            viewModel.deleteTask(task)
                        }
                        taskToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}