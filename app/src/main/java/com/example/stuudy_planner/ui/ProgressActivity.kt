package com.example.stuudy_planner.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.stuudy_planner.R
import com.example.stuudy_planner.data.TaskEntity
import com.example.stuudy_planner.viewmodel.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator

class ProgressActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels()

    private lateinit var tvProgressTitle: TextView
    private lateinit var tvCompletedRatio: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var tvMotivational: TextView
    private lateinit var tvTotalTasksCount: TextView
    private lateinit var tvCompletedTasksCount: TextView
    private lateinit var tvPendingTasksCount: TextView
    private lateinit var tvHighPriorityCount: TextView
    private lateinit var layoutSubjectBreakdown: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        initViews()
        setupListeners()
        observeData()
    }

    private fun initViews() {
        tvProgressTitle = findViewById(R.id.tvProgressTitle)
        tvCompletedRatio = findViewById(R.id.tvCompletedRatio)
        progressBar = findViewById(R.id.progressBar)
        tvMotivational = findViewById(R.id.tvMotivational)
        tvTotalTasksCount = findViewById(R.id.tvTotalTasksCount)
        tvCompletedTasksCount = findViewById(R.id.tvCompletedTasksCount)
        tvPendingTasksCount = findViewById(R.id.tvPendingTasksCount)
        tvHighPriorityCount = findViewById(R.id.tvHighPriorityCount)
        layoutSubjectBreakdown = findViewById(R.id.layoutSubjectBreakdown)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        bottomNavigation.selectedItemId = R.id.nav_progress
    }

    private fun setupListeners() {
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
                R.id.nav_tasks -> {
                    val intent = Intent(this, TasksActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_progress -> true
                else -> false
            }
        }
    }

    private fun observeData() {
        viewModel.allTasks.observe(this) { tasks ->
            val taskList = tasks ?: emptyList()
            updateStats(taskList)
        }
    }

    private fun updateStats(tasks: List<TaskEntity>) {
        val total = tasks.size
        val completed = tasks.count { it.completed }
        val pending = total - completed
        val highPriority = tasks.count { it.priority.equals("High", ignoreCase = true) }
        val percentage = if (total > 0) (completed * 100) / total else 0

        tvProgressTitle.text = "Study Progress: $percentage%"
        tvCompletedRatio.text = "Completed: $completed / $total Tasks"
        progressBar.progress = percentage

        tvTotalTasksCount.text = total.toString()
        tvCompletedTasksCount.text = completed.toString()
        tvPendingTasksCount.text = pending.toString()
        tvHighPriorityCount.text = highPriority.toString()

        // Motivational text based on progress
        tvMotivational.text = when {
            total == 0 -> "Tap + to add your study goals for today!"
            percentage == 100 -> "Outstanding! You've accomplished all your study goals!"
            percentage >= 75 -> "Almost there! Keep up the incredible effort!"
            percentage >= 50 -> "Halfway done! Keep pushing forward!"
            percentage > 0 -> "Good start! Keep going, one task at a time."
            else -> "Start ticking off your tasks to build study momentum!"
        }

        renderSubjectBreakdown(tasks)
    }

    private fun renderSubjectBreakdown(tasks: List<TaskEntity>) {
        layoutSubjectBreakdown.removeAllViews()

        if (tasks.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "No study tasks recorded yet."
                setTextColor(getColor(R.color.text_muted))
                textSize = 14f
                setPadding(0, 8, 0, 8)
            }
            layoutSubjectBreakdown.addView(emptyTv)
            return
        }

        val subjectGroups = tasks.groupBy { it.subject.trim() }

        for ((subject, subjectTasks) in subjectGroups) {
            val totalForSubject = subjectTasks.size
            val completedForSubject = subjectTasks.count { it.completed }
            val subjectPercent = (completedForSubject * 100) / totalForSubject

            val card = com.google.android.material.card.MaterialCardView(this).apply {
                radius = 24f
                cardElevation = 2f
                setCardBackgroundColor(getColor(R.color.card_surface))
                strokeColor = getColor(R.color.border_color)
                strokeWidth = 2
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
                layoutParams = params
            }

            val container = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 24, 32, 24)
            }

            val headerRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = 1f
            }

            val titleView = TextView(this).apply {
                text = subject
                setTextColor(getColor(R.color.text_primary))
                textSize = 15f
                paintFlags = paintFlags or android.graphics.Paint.FAKE_BOLD_TEXT_FLAG
                val p = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                layoutParams = p
            }

            val ratioView = TextView(this).apply {
                text = "$completedForSubject / $totalForSubject ($subjectPercent%)"
                setTextColor(getColor(R.color.text_secondary))
                textSize = 13f
            }

            headerRow.addView(titleView)
            headerRow.addView(ratioView)

            val miniProgress = LinearProgressIndicator(this).apply {
                progress = subjectPercent
                trackColor = getColor(R.color.primary_light)
                setIndicatorColor(getColor(R.color.primary))
                trackThickness = 12
                trackCornerRadius = 6
                val p = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 0)
                }
                layoutParams = p
            }

            container.addView(headerRow)
            container.addView(miniProgress)
            card.addView(container)
            layoutSubjectBreakdown.addView(card)
        }
    }
}
