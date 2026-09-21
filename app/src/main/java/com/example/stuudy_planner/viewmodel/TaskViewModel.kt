package com.example.stuudy_planner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.stuudy_planner.data.AppDatabase
import com.example.stuudy_planner.data.TaskEntity
import com.example.stuudy_planner.repository.TaskRepository
import com.example.stuudy_planner.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val allTasks: LiveData<List<TaskEntity>>
    val todayTasks: LiveData<List<TaskEntity>>

    init {
        val taskDao = AppDatabase.getDatabase(application, viewModelScope).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
        todayTasks = repository.getTasksByDate(DateTimeUtils.getTodayDate())
    }

    fun getTaskById(id: Long): LiveData<TaskEntity?> {
        return repository.getTaskById(id)
    }

    suspend fun getTaskByIdSync(id: Long): TaskEntity? {
        return repository.getTaskByIdSync(id)
    }

    fun insert(task: TaskEntity, onResult: ((Long) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = repository.insert(task)
            onResult?.invoke(newId)
        }
    }

    fun update(task: TaskEntity, onResult: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(task)
            onResult?.invoke()
        }
    }

    fun delete(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(task)
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(id)
        }
    }

    fun setTaskCompleted(id: Long, completed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setCompleted(id, completed)
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setCompleted(task.id, !task.completed)
        }
    }
}
