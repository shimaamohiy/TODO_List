package com.example.todo_list.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo_list.data.TaskRepository

class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val context: Context? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository, context) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
