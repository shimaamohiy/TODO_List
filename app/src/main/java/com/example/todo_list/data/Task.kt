
package com.example.todo_list.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @field:PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val description: String = "",

    val priority: String = "Medium",

    val category: String = "Personal",

    val dueDate: Long? = null,

    val isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)
