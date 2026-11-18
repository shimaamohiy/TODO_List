package com.example.todo_list

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo_list.data.TaskDatabase
import com.example.todo_list.data.TaskRepository
import com.example.todo_list.notifications.NotificationHelper
import com.example.todo_list.ui.HomeScreen
import com.example.todo_list.viewmodel.TaskViewModel
import com.example.todo_list.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {

    private var incomingSharedText by mutableStateOf<String?>(null)

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // Permission result
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        handleIncomingIntent(intent)

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
                viewModel = taskViewModel,
                initialSharedText = incomingSharedText
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            incomingSharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        }
    }
}