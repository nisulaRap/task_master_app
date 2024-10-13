package com.example.taskmasterapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.taskmasterapp.models.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * from task_table order by id ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("UPDATE task_table set name = :name, description = :description, priority = :priority, status = :status, deadlineDate = :deadlineDate, deadlineTime = :deadlineTime where id = :id")
    suspend fun update(id: Int?, name: String?, description: String?, priority: String?, status: String?, deadlineDate: String?, deadlineTime: String?): Int


    @Query("SELECT * FROM task_table WHERE name LIKE :query OR priority LIKE :query")
    fun searchTaskList(query: String): LiveData<List<Task>>

    @Query("SELECT * FROM task_table ORDER BY priority ASC")
    fun getTasksSortedByPriority(): LiveData<List<Task>>

    @Query("SELECT * FROM task_table ORDER BY deadlineDate ASC")
    fun getTasksSortedByDeadline(): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE priority = :priority")
    fun getTasksFilteredByPriority(priority: String): LiveData<List<Task>>
}