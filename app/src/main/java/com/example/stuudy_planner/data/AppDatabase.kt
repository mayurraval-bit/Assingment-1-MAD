package com.example.stuudy_planner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.stuudy_planner.utils.DateTimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_study_planner_db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialSampleData(database.taskDao())
                    }
                }
            }

            private suspend fun populateInitialSampleData(taskDao: TaskDao) {
                val today = DateTimeUtils.getTodayDate()
                val sampleTasks = listOf(
                    TaskEntity(
                        title = "Complete Python Assignment",
                        subject = "Python",
                        description = "Implement binary search tree and write unit tests.",
                        date = today,
                        startTime = "09:00 AM",
                        endTime = "10:30 AM",
                        priority = "High",
                        completed = false,
                        reminder = true
                    ),
                    TaskEntity(
                        title = "Study Computer Networks",
                        subject = "CN",
                        description = "Review OSI model layers and TCP/IP three-way handshake.",
                        date = today,
                        startTime = "11:00 AM",
                        endTime = "12:30 PM",
                        priority = "Medium",
                        completed = false,
                        reminder = false
                    ),
                    TaskEntity(
                        title = "Android Practical",
                        subject = "MAD",
                        description = "Build student study planner UI layouts and Room database.",
                        date = today,
                        startTime = "02:00 PM",
                        endTime = "04:00 PM",
                        priority = "High",
                        completed = true,
                        reminder = true
                    ),
                    TaskEntity(
                        title = "Revise Machine Learning",
                        subject = "ML",
                        description = "Go over gradient descent formulas and loss functions.",
                        date = today,
                        startTime = "05:00 PM",
                        endTime = "06:00 PM",
                        priority = "Low",
                        completed = false,
                        reminder = false
                    )
                )

                for (task in sampleTasks) {
                    taskDao.insertTask(task)
                }
            }
        }
    }
}
