
package com.example.todo_list.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: TaskDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            TaskDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        taskDao = database.taskDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertTaskAndGetAllTasks() = runBlocking {
        val task = Task(
            title = "Study Android",
            description = "Finish Room database",
            priority = "High"
        )

        taskDao.insertTask(task)

        val tasks = taskDao.getAllTasks().first()

        assertEquals(1, tasks.size)
        assertEquals("Study Android", tasks[0].title)
    }

    @Test
    fun updateTask() = runBlocking {
        val task = Task(title = "Old Task")

        taskDao.insertTask(task)

        val insertedTask = taskDao.getAllTasks().first()[0]

        val updatedTask = insertedTask.copy(
            title = "Updated Task"
        )

        taskDao.updateTask(updatedTask)

        val tasks = taskDao.getAllTasks().first()

        assertEquals("Updated Task", tasks[0].title)
    }

    @Test
    fun deleteTask() = runBlocking {
        val task = Task(title = "Delete Me")

        taskDao.insertTask(task)

        val insertedTask = taskDao.getAllTasks().first()[0]

        taskDao.deleteTask(insertedTask)

        val tasks = taskDao.getAllTasks().first()

        assertTrue(tasks.isEmpty())
    }

    @Test
    fun getCompletedAndPendingTasks() = runBlocking {
        taskDao.insertTask(
            Task(title = "Completed", isCompleted = true)
        )

        taskDao.insertTask(
            Task(title = "Pending", isCompleted = false)
        )

        val completed = taskDao.getCompletedTasks().first()
        val pending = taskDao.getPendingTasks().first()

        assertEquals(1, completed.size)
        assertEquals(1, pending.size)
        assertEquals("Completed", completed[0].title)
        assertEquals("Pending", pending[0].title)
    }

    @Test
    fun deleteAllTasks() = runBlocking {
        taskDao.insertTask(Task(title = "Task 1"))
        taskDao.insertTask(Task(title = "Task 2"))

        taskDao.deleteAllTasks()

        val tasks = taskDao.getAllTasks().first()

        assertTrue(tasks.isEmpty())
    }

@Test
fun getTaskById() = runBlocking {
    taskDao.insertTask(Task(title = "Find Me"))

    val insertedTask = taskDao.getAllTasks().first()[0]
    val result = taskDao.getTaskById(insertedTask.id).first()

    assertNotNull(result)
    assertEquals("Find Me", result?.title)
}

@Test
fun getTasksBetween() = runBlocking {
    val start = 1_800_000_000_000L
    val end = start + 86_400_000L

    taskDao.insertTask(
        Task(title = "Today Task", dueDate = start + 1000)
    )
    taskDao.insertTask(
        Task(title = "Tomorrow Task", dueDate = end + 1000)
    )
    taskDao.insertTask(
        Task(title = "No Date Task", dueDate = null)
    )

    val tasks = taskDao.getTasksBetween(start, end).first()

    assertEquals(1, tasks.size)
    assertEquals("Today Task", tasks[0].title)
}

@Test
fun getTotalCount() = runBlocking {
    taskDao.insertTask(Task(title = "Task 1"))
    taskDao.insertTask(Task(title = "Task 2"))
    taskDao.insertTask(Task(title = "Task 3"))

    val count = taskDao.getTotalCount().first()

    assertEquals(3, count)
}

@Test
fun getCompletedCount() = runBlocking {
    taskDao.insertTask(Task(title = "Done 1", isCompleted = true))
    taskDao.insertTask(Task(title = "Done 2", isCompleted = true))
    taskDao.insertTask(Task(title = "Pending", isCompleted = false))

    val count = taskDao.getCompletedCount().first()

    assertEquals(2, count)
}

@Test
fun getCategoriesCount() = runBlocking {
    taskDao.insertTask(Task(title = "Task 1", category = "Work"))
    taskDao.insertTask(Task(title = "Task 2", category = "Work"))
    taskDao.insertTask(Task(title = "Task 3", category = "Personal"))

    val count = taskDao.getCategoriesCount().first()

    assertEquals(2, count)
}

@Test
fun searchTasks() = runBlocking {
    taskDao.insertTask(
        Task(title = "Study Kotlin", description = "Android development")
    )
    taskDao.insertTask(
        Task(title = "Buy groceries", description = "Shopping")
    )

    val results = taskDao.searchTasks("Kotlin").first()

    assertEquals(1, results.size)
    assertEquals("Study Kotlin", results[0].title)
}

@Test
fun setCompleted() = runBlocking {
    taskDao.insertTask(Task(title = "My Task", isCompleted = false))

    val insertedTask = taskDao.getAllTasks().first()[0]

    taskDao.setCompleted(insertedTask.id, true)

    val updatedTask = taskDao.getTaskById(insertedTask.id).first()

    assertEquals(true, updatedTask?.isCompleted)
}

}
