package com.example.taskmasterapp.database

import androidx.lifecycle.LiveData
import com.example.taskmasterapp.models.Task

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task){
        taskDao.insert(task)
    }

    suspend fun delete(task: Task){
        taskDao.delete(task)
    }

    suspend fun update(task: Task){
        taskDao.update(task.id, task.name, task.description, task.priority, task.status, task.deadlineDate, task.deadlineTime)
    }

    // Function to retrieve search results from the DAO (database) based on the query
    fun searchTasks(query: String): LiveData<List<Task>> {
        return taskDao.searchTaskList(query) // Query the DAO for matching tasks
    }
}