package com.example.taskmasterapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "priority") val priority: String?,
    @ColumnInfo(name = "status") val status: String?,
    @ColumnInfo(name = "deadlineDate") val deadlineDate: String?,
    @ColumnInfo(name = "deadlineTime") val deadlineTime: String?
): java.io.Serializable