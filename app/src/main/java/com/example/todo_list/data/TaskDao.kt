
package com.example.todo_list.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Get all tasks
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    // Get a specific task by ID
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): Flow<Task?>

    // Insert a new task
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    // Update an existing task
    @Update
    suspend fun updateTask(task: Task)

    // Delete a task
    @Delete
    suspend fun deleteTask(task: Task)

    // Delete all tasks
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    // Get completed tasks
    @Query("SELECT * FROM tasks WHERE isCompleted = 1")
    fun getCompletedTasks(): Flow<List<Task>>

    // Get pending tasks
    @Query("SELECT * FROM tasks WHERE isCompleted = 0")
    fun getPendingTasks(): Flow<List<Task>>

    //Today Task
    @Query("SELECT * FROM tasks WHERE dueDate >= :start AND dueDate < :end ORDER BY isCompleted ASC, createdAt DESC")
    fun getTasksBetween(start: Long, end: Long): Flow<List<Task>>

    // Progress 6 out of 10
    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    // Total projects
    @Query("SELECT COUNT(DISTINCT category) FROM tasks")
    fun getCategoriesCount(): Flow<Int>

    //Search
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTasks(query: String): Flow<List<Task>>
     // checkbox
    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun setCompleted(taskId: Int, completed: Boolean)
}



