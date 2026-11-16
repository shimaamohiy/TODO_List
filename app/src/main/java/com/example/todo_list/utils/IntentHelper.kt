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

/**
 * Helper object demonstrating Explicit and Implicit Intents in Android.
 *
 * 1. Explicit Intent: Directly specifies the target component class
 *    (e.g., TaskDetailActivity::class.java).
 *
 * 2. Implicit Intent: Specifies a general action (ACTION_SEND, ACTION_WEB_SEARCH, ACTION_INSERT)
 *    allowing the Android OS or external apps to handle the request.
 */
object IntentHelper {

    // ==========================================
    // 1. EXPLICIT INTENT
    // ==========================================

    /**
     * Navigates explicitly to [TaskDetailActivity] by specifying its class name.
     * Passes the target task ID through Intent Extras.
     */
    fun openTaskDetail(context: Context, taskId: Int) {
        val intent = Intent(context, TaskDetailActivity::class.java).apply {
            putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId)
        }
        context.startActivity(intent)
    }

    // ==========================================
    // 2. IMPLICIT INTENTS
    // ==========================================

    /**
     * Implicit Intent: Shares task details with external apps (WhatsApp, Gmail, Notes, etc.)
     * using [Intent.ACTION_SEND] wrapped inside an Intent Chooser.
     */
    fun shareTask(context: Context, task: Task) {
        val shareMessage = buildString {
            appendLine("📌 Task: ${task.title}")
            if (task.description.isNotBlank()) {
                appendLine("📝 Description: ${task.description}")
            }
            appendLine("🏷️ Category: ${task.category}")
            appendLine("⚡ Priority: ${task.priority}")
            task.dueDate?.let {
                val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                appendLine("⏰ Due Date: $formattedDate")
            }
            appendLine(if (task.isCompleted) "✅ Status: Completed" else "⏳ Status: Pending")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "TODO: ${task.title}")
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }

        val chooserIntent = Intent.createChooser(sendIntent, "Share Task via")
        try {
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "No app available to share this task", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Implicit Intent: Opens a web search for the task topic or research materials
     * using [Intent.ACTION_WEB_SEARCH] with fallback to browser [Intent.ACTION_VIEW].
     */
    fun searchTaskOnline(context: Context, query: String) {
        val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, query)
        }

        try {
            context.startActivity(searchIntent)
        } catch (e: Exception) {
            // Fallback to viewing search results in browser
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
            )
            try {
                context.startActivity(webIntent)
            } catch (fallbackEx: Exception) {
                Toast.makeText(context, "No browser available to perform search", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Implicit Intent: Adds task to the system calendar using [Intent.ACTION_INSERT]
     * targeting [CalendarContract.Events.CONTENT_URI].
     */
    fun addEventToCalendar(context: Context, task: Task) {
        val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, task.title)
            putExtra(CalendarContract.Events.DESCRIPTION, task.description)
            task.dueDate?.let {
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, it)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, it + 60 * 60 * 1000) // Default: 1 hour
            }
        }

        try {
            context.startActivity(calendarIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "No calendar app available on device", Toast.LENGTH_SHORT).show()
        }
    }
}
