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
}