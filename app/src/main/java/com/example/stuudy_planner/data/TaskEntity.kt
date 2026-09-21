package com.example.stuudy_planner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val description: String = "",
    val date: String, // Format: yyyy-MM-dd
    val startTime: String = "",
    val endTime: String = "",
    val priority: String = "Medium", // "Low", "Medium", "High"
    val completed: Boolean = false,
    val reminder: Boolean = false
)
