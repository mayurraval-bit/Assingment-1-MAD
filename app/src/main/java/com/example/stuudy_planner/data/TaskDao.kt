package com.example.stuudy_planner.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY date ASC, startTime ASC")
    fun getAllTasks(): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY startTime ASC")
    fun getTasksByDate(date: String): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    fun getTaskById(id: Long): LiveData<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    fun getTaskByIdSync(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: TaskEntity): Long

    @Update
    fun updateTask(task: TaskEntity): Int

    @Delete
    fun deleteTask(task: TaskEntity): Int

    @Query("DELETE FROM tasks WHERE id = :id")
    fun deleteTaskById(id: Long): Int

    @Query("UPDATE tasks SET completed = :completed WHERE id = :id")
    fun updateTaskCompletion(id: Long, completed: Boolean): Int

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTaskCount(): Int
}
