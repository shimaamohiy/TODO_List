package com.example.todo_list.notifications


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val taskId = intent.getIntExtra(
            "task_id",
            0
        )

        val taskTitle = intent.getStringExtra(
            "task_title"
        ) ?: "Your task"

        NotificationHelper.showNotification(
            context,
            taskId,
            taskTitle
        )
    }
}