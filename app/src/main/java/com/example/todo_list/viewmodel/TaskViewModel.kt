package com.example.todo_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_list.data.Task
import com.example.todo_list.data.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    val allTasks: StateFlow<List<Task>> =
        repository.allTasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val completedTasks: StateFlow<List<Task>> =
        repository.completedTasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val pendingTasks: StateFlow<List<Task>> =
        repository.pendingTasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val totalCount =
        repository.totalCount.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val completedCount =
        repository.completedCount.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val projectsCount =
        repository.categoriesCount.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val progress: StateFlow<Float> =
        combine(completedCount, totalCount) { done, total ->
            if (total == 0) 0f
            else done.toFloat() / total
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0f
        )

    // Search

    val searchQuery = MutableStateFlow("")

    fun updateSearch(query: String) {
        searchQuery.value = query
    }

    // Filters

    val selectedPriority = MutableStateFlow("All")

    val selectedCategory = MutableStateFlow("All")

    fun updatePriority(priority: String) {
        selectedPriority.value = priority
    }

    fun updateCategory(category: String) {
        selectedCategory.value = category
    }

    fun clearFilters() {
        searchQuery.value = ""
        selectedPriority.value = "All"
        selectedCategory.value = "All"
    }

    // Combined Search + Filters

    val filteredTasks: StateFlow<List<Task>> =
        combine(
            allTasks,
            searchQuery,
            selectedPriority,
            selectedCategory
        ) { tasks, query, priority, category ->

            tasks.filter { task ->

                val matchesSearch =
                    task.title.contains(query, ignoreCase = true) ||
                            task.description.contains(query, ignoreCase = true)

                val matchesPriority =
                    priority == "All" ||
                            task.priority.equals(priority, ignoreCase = true)

                val matchesCategory =
                    category == "All" ||
                            task.category.equals(category, ignoreCase = true)

                matchesSearch && matchesPriority && matchesCategory
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Selected Day

    private val selectedDay =
        MutableStateFlow(startOfDay(System.currentTimeMillis()))

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForSelectedDay: StateFlow<List<Task>> =
        selectedDay.flatMapLatest { start ->

            repository.getTasksBetween(
                start,
                nextDay(start)
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun selectDay(dayStart: Long) {
        selectedDay.value = dayStart
    }

    // Task Operations

    fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            repository.deleteAllTasks()
        }
    }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch {
            repository.setCompleted(
                task.id,
                !task.isCompleted
            )
        }
    }

    fun search(query: String) =
        repository.searchTasks(query)

    fun getTaskById(taskId: Int): kotlinx.coroutines.flow.Flow<Task?> =
        repository.getTaskById(taskId)

    private fun startOfDay(time: Long): Long {

        return Calendar.getInstance().apply {
            timeInMillis = time

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

        }.timeInMillis
    }

    private fun nextDay(start: Long): Long {

        return Calendar.getInstance().apply {
            timeInMillis = start
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis
    }
}