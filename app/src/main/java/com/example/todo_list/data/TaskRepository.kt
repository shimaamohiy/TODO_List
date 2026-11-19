
package com.example.todo_list.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    // Get all tasks
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    // Get completed tasks
    val completedTasks: Flow<List<Task>> = taskDao.getCompletedTasks()

    // Get pending tasks
    val pendingTasks: Flow<List<Task>> = taskDao.getPendingTasks()

    // Get task by ID
    fun getTaskById(taskId: Int): Flow<Task?> {
        return taskDao.getTaskById(taskId)
    }
    val totalCount: Flow<Int> = taskDao.getTotalCount()
    val completedCount: Flow<Int> = taskDao.getCompletedCount()
    val categoriesCount: Flow<Int> = taskDao.getCategoriesCount()

    fun getTasksBetween(start: Long, end: Long) = taskDao.getTasksBetween(start, end)
    fun searchTasks(query: String) = taskDao.searchTasks(query)
    suspend fun setCompleted(id: Int, completed: Boolean) = taskDao.setCompleted(id, completed)
    // Insert a new task
    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    // Update an existing task
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    // Delete a task
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    // Delete all tasks
    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }
}