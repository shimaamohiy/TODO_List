package com.example.todo_list.utils

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import com.example.todo_list.data.Task
import com.example.todo_list.ui.TaskDetailActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object IntentHelper {

    fun openTaskDetail(context: Context, taskId: Int) {
        val intent = Intent(context, TaskDetailActivity::class.java).apply {
            putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId)
        }
        context.startActivity(intent)
    }

    fun shareTask(context: Context, task: Task) {
        val shareMessage = buildString {
            appendLine("Task: ${task.title}")
            if (task.description.isNotBlank()) {
                appendLine("Description: ${task.description}")
            }
            appendLine("Category: ${task.category}")
            appendLine("Priority: ${task.priority}")
            task.dueDate?.let {
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                appendLine("Due Date: $formattedDate")
            }
            appendLine("Status: ${if (task.isCompleted) "Completed" else "Pending"}")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, task.title)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }

        try {
            context.startActivity(Intent.createChooser(sendIntent, "Share Task"))
        } catch (e: Exception) {
            Toast.makeText(context, "No app available to share", Toast.LENGTH_SHORT).show()
        }
    }

    fun searchTaskOnline(context: Context, query: String) {
        val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, query)
        }

        try {
            context.startActivity(searchIntent)
        } catch (e: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
            )
            try {
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "No browser found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addEventToCalendar(context: Context, task: Task) {
        val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, task.title)
            putExtra(CalendarContract.Events.DESCRIPTION, task.description)
            task.dueDate?.let {
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, it)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, it + 60 * 60 * 1000)
            }
        }

        try {
            context.startActivity(calendarIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "No calendar app found", Toast.LENGTH_SHORT).show()
        }
    }
}
