package com.example.todo_list.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todo_list.MainActivity
import com.example.todo_list.R
import com.example.todo_list.data.Task
import com.example.todo_list.ui.TaskDetailActivity
import java.util.Calendar

object NotificationHelper {

    private const val CHANNEL_ID = "task_reminders"

    fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming tasks"
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(
        context: Context,
        task: Task
    ) {

        val dueDate = task.dueDate ?: return

        if (task.isCompleted) return

        val reminderTime = Calendar.getInstance().apply {
            timeInMillis = dueDate
        }.timeInMillis

        if (reminderTime <= System.currentTimeMillis()) return

        val intent = Intent(
            context,
            ReminderReceiver::class.java
        ).apply {

            putExtra("task_id", task.id)
            putExtra("task_title", task.title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )
    }

    fun cancelReminder(
        context: Context,
        taskId: Int
    ) {

        val intent = Intent(
            context,
            ReminderReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }

    fun showNotification(
        context: Context,
        taskId: Int,
        taskTitle: String
    ) {

        createNotificationChannel(context)


        val intent = Intent(
            context,
            TaskDetailActivity::class.java
        ).apply {
            putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Task Reminder ")
            .setContentText("Don't forget: $taskTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {

            NotificationManagerCompat
                .from(context)
                .notify(taskId, notification)

        } catch (_: SecurityException) {

        }
    }
}