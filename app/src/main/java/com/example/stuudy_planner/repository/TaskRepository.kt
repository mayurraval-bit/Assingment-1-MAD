package com.example.stuudy_planner.repository

import androidx.lifecycle.LiveData
import com.example.stuudy_planner.data.TaskDao
import com.example.stuudy_planner.data.TaskEntity

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksByDate(date: String): LiveData<List<TaskEntity>> {
        return taskDao.getTasksByDate(date)
    }

    fun getTaskById(id: Long): LiveData<TaskEntity?> {
        return taskDao.getTaskById(id)
    }

    suspend fun getTaskByIdSync(id: Long): TaskEntity? {
        return taskDao.getTaskByIdSync(id)
    }

    suspend fun insert(task: TaskEntity): Long {
        return taskDao.insertTask(task)
    }

    suspend fun update(task: TaskEntity): Int {
        return taskDao.updateTask(task)
    }

    suspend fun delete(task: TaskEntity): Int {
        return taskDao.deleteTask(task)
    }

    suspend fun deleteById(id: Long): Int {
        return taskDao.deleteTaskById(id)
    }

    suspend fun setCompleted(id: Long, completed: Boolean): Int {
        return taskDao.updateTaskCompletion(id, completed)
    }
}
