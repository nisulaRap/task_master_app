package com.example.taskmasterapp.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmasterapp.database.TaskDatabase
import com.example.taskmasterapp.database.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TaskRepository
    val allTask : LiveData<List<Task>>

    init {
        val dao = TaskDatabase.getDatabase(application).getTaskDao()
        repository = TaskRepository(dao)
        allTask = repository.allTasks
    }

    fun insertTask(task: Task) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch(Dispatchers.IO){
        repository.update(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch(Dispatchers.IO){
        repository.delete(task)
    }

    // Function to search todos based on the search query
    fun searchTask(query: String): LiveData<List<Task>> {
        return repository.searchTasks(query) // Delegate the search operation to the repository
    }

    // Function to sort tasks by priority
    fun sortTasksByPriority(): LiveData<List<Task>> {
        return repository.sortTasksByPriority() // This should call the repository method
    }

    // Function to sort tasks by deadline
    fun sortTasksByDeadline(): LiveData<List<Task>> {
        return repository.sortTasksByDeadline() // This should call the repository method
    }

    // Add filtering function based on task priority (high, medium, low)
    fun filterTasksByPriority(priority: String): LiveData<List<Task>> {
        return repository.filterTasksByPriority(priority) // This should call the repository method
    }
}