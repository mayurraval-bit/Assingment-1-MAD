package com.example.stuudy_planner.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stuudy_planner.R
import com.example.stuudy_planner.adapter.TaskAdapter
import com.example.stuudy_planner.data.TaskEntity
import com.example.stuudy_planner.utils.ReminderHelper
import com.example.stuudy_planner.viewmodel.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class TasksActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels()

    private lateinit var rvTasks: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var etSearch: EditText
    private lateinit var btnClearSearch: ImageButton
    private lateinit var chipGroupFilters: ChipGroup
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView

    private var allTaskList: List<TaskEntity> = emptyList()
    private var currentFilter: String = "ALL"
    private var currentSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        initViews()
        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun initViews() {
        rvTasks = findViewById(R.id.rvTasks)
        etSearch = findViewById(R.id.etSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        chipGroupFilters = findViewById(R.id.chipGroupFilters)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        bottomNavigation.selectedItemId = R.id.nav_tasks

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

        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = adapter
    }

    private fun setupListeners() {
        // Search text change listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString()?.trim() ?: ""
                btnClearSearch.visibility = if (currentSearchQuery.isNotEmpty()) View.VISIBLE else View.GONE
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnClearSearch.setOnClickListener {
            etSearch.text.clear()
        }

        // Filter chips listener
        chipGroupFilters.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when {
                checkedIds.contains(R.id.chipFilterPending) -> "PENDING"
                checkedIds.contains(R.id.chipFilterCompleted) -> "COMPLETED"
                checkedIds.contains(R.id.chipFilterHighPriority) -> "HIGH_PRIORITY"
                else -> "ALL"
            }
            applyFilters()
        }

        // Bottom Navigation
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_tasks -> true
                R.id.nav_progress -> {
                    val intent = Intent(this, ProgressActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeData() {
        viewModel.allTasks.observe(this) { tasks ->
            allTaskList = tasks ?: emptyList()
            applyFilters()
        }
    }

    private fun applyFilters() {
        var filteredList = allTaskList

        // Apply status / priority filter
        filteredList = when (currentFilter) {
            "PENDING" -> filteredList.filter { !it.completed }
            "COMPLETED" -> filteredList.filter { it.completed }
            "HIGH_PRIORITY" -> filteredList.filter { it.priority.equals("High", ignoreCase = true) }
            else -> filteredList
        }

        // Apply text query search
        if (currentSearchQuery.isNotEmpty()) {
            val queryLower = currentSearchQuery.lowercase()
            filteredList = filteredList.filter {
                it.title.lowercase().contains(queryLower) ||
                it.subject.lowercase().contains(queryLower) ||
                it.description.lowercase().contains(queryLower)
            }
        }

        adapter.submitList(filteredList)

        // Show/hide empty state
        if (filteredList.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            rvTasks.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            rvTasks.visibility = View.VISIBLE
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
