package com.example.todo_list

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo_list.data.TaskDatabase
import com.example.todo_list.data.TaskRepository
import com.example.todo_list.notifications.NotificationHelper
import com.example.todo_list.ui.HomeScreen
import com.example.todo_list.viewmodel.TaskViewModel
import com.example.todo_list.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // Permission result
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= 33) {

            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        val database = TaskDatabase.getDatabase(
            applicationContext
        )

        val repository = TaskRepository(
            database.taskDao()
        )

        val factory = TaskViewModelFactory(
            repository
        )

        setContent {

            val taskViewModel: TaskViewModel = viewModel(
                factory = factory
            )

            HomeScreen(
                viewModel = taskViewModel
            )
        }
    }
}