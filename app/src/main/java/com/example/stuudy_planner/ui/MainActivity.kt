package com.example.stuudy_planner.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stuudy_planner.R
import com.example.stuudy_planner.adapter.TaskAdapter
import com.example.stuudy_planner.data.TaskEntity
import com.example.stuudy_planner.utils.DateTimeUtils
import com.example.stuudy_planner.utils.ReminderHelper
import com.example.stuudy_planner.viewmodel.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels()

    private lateinit var tvGreeting: TextView
    private lateinit var tvDateHeader: TextView
    private lateinit var tvTodayCount: TextView
    private lateinit var tvCompletedCount: TextView
    private lateinit var tvPendingCount: TextView
    private lateinit var tvProgressPercent: TextView
    private lateinit var rvTodayTasks: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupListeners()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun initViews() {
        tvGreeting = findViewById(R.id.tvGreeting)
        tvDateHeader = findViewById(R.id.tvDateHeader)
        tvTodayCount = findViewById(R.id.tvTodayCount)
        tvCompletedCount = findViewById(R.id.tvCompletedCount)
        tvPendingCount = findViewById(R.id.tvPendingCount)
        tvProgressPercent = findViewById(R.id.tvProgressPercent)
        rvTodayTasks = findViewById(R.id.rvTodayTasks)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Set dynamic greeting and date header
        tvGreeting.text = DateTimeUtils.getGreeting()
        val dateFormat = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
        tvDateHeader.text = dateFormat.format(Date())

        findViewById<ExtendedFloatingActionButton>(R.id.fabAddTask).setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onTaskClick = { task ->
                val intent = Intent(this, EditTaskActivity::class.java).apply {
                    putExtra("TASK_ID", task.id)
                }
                startActivity(intent)
            },
            onCompletionToggle = { task, isCompleted ->
                viewModel.setTaskCompleted(task.id, isCompleted)
            },
            onEditClick = { task ->
                val intent = Intent(this, EditTaskActivity::class.java).apply {
                    putExtra("TASK_ID", task.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { task ->
                showDeleteConfirmationDialog(task)
            }
        )

        rvTodayTasks.layoutManager = LinearLayoutManager(this)
        rvTodayTasks.adapter = adapter
    }

    private fun setupListeners() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_tasks -> {
                    val intent = Intent(this, TasksActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_progress -> {
                    val intent = Intent(this, ProgressActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    private fun observeData() {
        // Observe today's tasks
        viewModel.todayTasks.observe(this) { todayTasks ->
            val list = todayTasks ?: emptyList()
            adapter.submitList(list)

            tvTodayCount.text = list.size.toString()

            if (list.isEmpty()) {
                layoutEmptyState.visibility = View.VISIBLE
                rvTodayTasks.visibility = View.GONE
            } else {
                layoutEmptyState.visibility = View.GONE
                rvTodayTasks.visibility = View.VISIBLE
            }
        }

        // Observe all tasks for overall summary metrics
        viewModel.allTasks.observe(this) { allTasks ->
            val list = allTasks ?: emptyList()
            val total = list.size
            val completed = list.count { it.completed }
            val pending = total - completed
            val percentage = if (total > 0) (completed * 100) / total else 0

            tvCompletedCount.text = completed.toString()
            tvPendingCount.text = pending.toString()
            tvProgressPercent.text = "$percentage%"
        }
    }

    private fun showDeleteConfirmationDialog(task: TaskEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                ReminderHelper.cancelReminder(this, task.id)
                viewModel.delete(task)
                Toast.makeText(this, R.string.task_deleted_success, Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
