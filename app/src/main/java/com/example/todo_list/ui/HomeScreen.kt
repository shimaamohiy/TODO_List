package com.example.todo_list.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.todo_list.data.Task
import com.example.todo_list.viewmodel.TaskViewModel
import java.util.Calendar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TaskViewModel
) {

    val allTasks by viewModel.allTasks.collectAsState()

    val visibleTasks by viewModel.filteredTasks.collectAsState()

    val totalCount by viewModel.totalCount.collectAsState()

    val completedCount by viewModel.completedCount.collectAsState()

    val progress by viewModel.progress.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()

    val selectedPriority by viewModel.selectedPriority.collectAsState()

    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var selectedItem by remember {
        mutableStateOf("Home")
    }

    var showAddEditScreen by remember {
        mutableStateOf(false)
    }

    var selectedTask by remember {
        mutableStateOf<Task?>(null)
    }


    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val tomorrowStart = Calendar.getInstance().apply {
        timeInMillis = todayStart
        add(Calendar.DAY_OF_YEAR, 1)
    }.timeInMillis

    val tasksToDisplay = when (selectedItem) {

        "Completed" ->
            visibleTasks.filter { it.isCompleted }

        "Tasks" ->
            visibleTasks

        else ->
            visibleTasks.filter { task ->
                !task.isCompleted &&
                        task.dueDate != null &&
                        task.dueDate >= todayStart &&
                        task.dueDate < tomorrowStart
            }
    }


    val categories = listOf(
        "All",
        "Personal",
        "Work",
        "Study",
        "Health",
        "Other"
    )

    val priorities = listOf(
        "All",
        "High",
        "Medium",
        "Low"
    )

    Scaffold(
        containerColor = Lavender,

        bottomBar = {

            BottomNavigation(
                selectedItem = selectedItem,
                onItemSelected = {
                    selectedItem = it
                }
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

                Text(
                    "+",
                    fontSize = 28.sp
                )
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(Modifier.height(20.dp))

            // HEADER

            Text(
                "Hello, Welcome! ",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = DeepPurple
            )

            Text(
                "Let's organize your day.",
                fontSize = 14.sp,
                color = GrayText
            )

            Spacer(Modifier.height(22.dp))

            // SUMMARY

            Text(
                "Your Overview",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MainText
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                SummaryCard(
                    "Total Tasks",
                    totalCount,
                    LightPurple,

                )

                SummaryCard(
                    "Completed",
                    completedCount,
                    LightPink,

                )

                SummaryCard(
                    "Pending",
                    totalCount - completedCount,
                    Color(0xFFE8E0FF),

                )
            }

            Spacer(Modifier.height(20.dp))

            // PROGRESS

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        AppWhite,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(16.dp)
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

                Spacer(Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp),
                    color = Pink,
                    trackColor = LightPurple,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            Spacer(Modifier.height(18.dp))

            // SEARCH

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    viewModel.updateSearch(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Search tasks...")
                },
                leadingIcon = {
                    Text("🔍")
                },
                trailingIcon = {

                    if (searchQuery.isNotEmpty()) {

                        IconButton(
                            onClick = {
                                viewModel.updateSearch("")
                            }
                        ) {
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

            Spacer(Modifier.height(12.dp))

            // PRIORITY FILTER

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
                        onClick = {
                            viewModel.updatePriority(priority)
                        },
                        label = {
                            Text(priority)
                        },
                        modifier = Modifier.padding(end = 7.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LightPurple,
                            selectedLabelColor = DeepPurple
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // CATEGORY FILTER

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
                        onClick = {
                            viewModel.updateCategory(category)
                        },
                        label = {
                            Text(category)
                        },
                        modifier = Modifier.padding(end = 7.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LightPink,
                            selectedLabelColor = DeepPurple
                        )
                    )
                }
            }

            Spacer(Modifier.height(15.dp))

            // TASK TITLE

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

            Spacer(Modifier.height(12.dp))

            // TASK LIST

            if (tasksToDisplay.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Text(
                            if (searchQuery.isNotEmpty() ||
                                selectedPriority != "All" ||
                                selectedCategory != "All"
                            ) "No matching tasks found"
                            else "No tasks yet!",
                            fontWeight = FontWeight.Bold,
                            color = DeepPurple,
                            fontSize = 17.sp
                        )

                        Text(
                            if (searchQuery.isNotEmpty() ||
                                selectedPriority != "All" ||
                                selectedCategory != "All"
                            ) "Try changing your search or filters."
                            else "Add your first task and get organized ",
                            color = GrayText,
                            fontSize = 13.sp
                        )

                        if (searchQuery.isNotEmpty() ||
                            selectedPriority != "All" ||
                            selectedCategory != "All"
                        ) {

                            TextButton(
                                onClick = {
                                    viewModel.clearFilters()
                                }
                            ) {
                                Text(
                                    "Clear Filters",
                                    color = DeepPurple
                                )
                            }
                        }
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {

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
                                    viewModel.toggleCompleted(task)
                                },

                                onClick = {
                                    selectedTask = task
                                    showAddEditScreen = true
                                },

                                onDelete = {
                                    viewModel.deleteTask(task)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // ADD / EDIT DIALOG

    if (showAddEditScreen) {

        Dialog(
            onDismissRequest = {
                showAddEditScreen = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(max = 700.dp),
                shape = RoundedCornerShape(24.dp),
                color = Lavender
            ) {

                AddEditTaskScreen(
                    task = selectedTask,

                    onSave = { task ->

                        if (selectedTask == null) {
                            viewModel.insertTask(task)
                        } else {
                            viewModel.updateTask(task)
                        }

                        showAddEditScreen = false
                    },

                    onCancel = {
                        showAddEditScreen = false
                    }
                )
            }
        }
    }
}