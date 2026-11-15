
package com.example.todo_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_list.data.Task
import com.example.todo_list.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    // Observe all tasks
    val allTasks: StateFlow<List<Task>> =
        repository.allTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Observe completed tasks
    val completedTasks: StateFlow<List<Task>> =
        repository.completedTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Observe pending tasks
    val pendingTasks: StateFlow<List<Task>> =
        repository.pendingTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Add task
    fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    // Update task
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    // Delete task
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Delete all tasks
    fun deleteAllTasks() {
        viewModelScope.launch {
            repository.deleteAllTasks()
        }
    }
    private val selectedDay = MutableStateFlow(startOfDay(System.currentTimeMillis()))

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForSelectedDay: StateFlow<List<Task>> =
        selectedDay.flatMapLatest { start ->
            repository.getTasksBetween(start, nextDay(start))
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount = repository.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val completedCount = repository.completedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val projectsCount = repository.categoriesCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val progress: StateFlow<Float> =
        combine(completedCount, totalCount) { done, total ->
            if (total == 0) 0f else done.toFloat() / total
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    fun selectDay(dayStart: Long) { selectedDay.value = dayStart }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch { repository.setCompleted(task.id, !task.isCompleted) }
    }

    fun search(query: String) = repository.searchTasks(query)

    private fun startOfDay(time: Long): Long = Calendar.getInstance().apply {
        timeInMillis = time
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun nextDay(start: Long): Long = Calendar.getInstance().apply {
        timeInMillis = start
        add(Calendar.DAY_OF_YEAR, 1)   //+24 hour
    }.timeInMillis
}
