package com.example.stuudy_planner.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.stuudy_planner.R
import com.example.stuudy_planner.data.TaskEntity
import com.example.stuudy_planner.utils.DateTimeUtils
import com.example.stuudy_planner.utils.ReminderHelper
import com.example.stuudy_planner.viewmodel.TaskViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class EditTaskActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels()

    private lateinit var tilTitle: TextInputLayout
    private lateinit var etTitle: TextInputEditText
    private lateinit var tilSubject: TextInputLayout
    private lateinit var etSubject: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedStartTime: TextView
    private lateinit var tvSelectedEndTime: TextView
    private lateinit var rgPriority: RadioGroup
    private lateinit var rbPriorityLow: RadioButton
    private lateinit var rbPriorityMedium: RadioButton
    private lateinit var rbPriorityHigh: RadioButton
    private lateinit var switchReminder: MaterialSwitch
    private lateinit var btnUpdateTask: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private var taskId: Long = 0L
    private var isCompleted: Boolean = false
    private var selectedDate: String = ""
    private var selectedStartTime: String = ""
    private var selectedEndTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        taskId = intent.getLongExtra("TASK_ID", 0L)
        if (taskId == 0L) {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        loadTaskData()
        setupListeners()
    }

    private fun initViews() {
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        tilTitle = findViewById(R.id.tilTitle)
        etTitle = findViewById(R.id.etTitle)
        tilSubject = findViewById(R.id.tilSubject)
        etSubject = findViewById(R.id.etSubject)
        etDescription = findViewById(R.id.etDescription)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvSelectedStartTime = findViewById(R.id.tvSelectedStartTime)
        tvSelectedEndTime = findViewById(R.id.tvSelectedEndTime)
        rgPriority = findViewById(R.id.rgPriority)
        rbPriorityLow = findViewById(R.id.rbPriorityLow)
        rbPriorityMedium = findViewById(R.id.rbPriorityMedium)
        rbPriorityHigh = findViewById(R.id.rbPriorityHigh)
        switchReminder = findViewById(R.id.switchReminder)
        btnUpdateTask = findViewById(R.id.btnUpdateTask)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun loadTaskData() {
        viewModel.getTaskById(taskId).observe(this) { task ->
            if (task == null) return@observe

            isCompleted = task.completed
            selectedDate = task.date
            selectedStartTime = task.startTime
            selectedEndTime = task.endTime

            etTitle.setText(task.title)
            etSubject.setText(task.subject)
            etDescription.setText(task.description)
            tvSelectedDate.text = DateTimeUtils.formatDisplayDate(task.date)

            tvSelectedStartTime.text = if (task.startTime.isNotBlank()) task.startTime else "Select Time"
            tvSelectedEndTime.text = if (task.endTime.isNotBlank()) task.endTime else "Select Time"

            when (task.priority.lowercase()) {
                "high" -> rbPriorityHigh.isChecked = true
                "low" -> rbPriorityLow.isChecked = true
                else -> rbPriorityMedium.isChecked = true
            }

            switchReminder.isChecked = task.reminder
        }
    }

    private fun setupListeners() {
        // Date Picker
        findViewById<MaterialCardView>(R.id.cardDatePicker).setOnClickListener {
            showDatePicker()
        }

        // Start Time Picker
        findViewById<MaterialCardView>(R.id.cardStartTimePicker).setOnClickListener {
            showStartTimePicker()
        }

        // End Time Picker
        findViewById<MaterialCardView>(R.id.cardEndTimePicker).setOnClickListener {
            showEndTimePicker()
        }

        // Cancel Button
        btnCancel.setOnClickListener {
            finish()
        }

        // Update Button
        btnUpdateTask.setOnClickListener {
            updateTask()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = DateTimeUtils.formatDate(selectedYear, selectedMonth, selectedDay)
            tvSelectedDate.text = DateTimeUtils.formatDisplayDate(selectedDate)
        }, year, month, day).show()
    }

    private fun showStartTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            selectedStartTime = DateTimeUtils.formatTime(selectedHour, selectedMinute)
            tvSelectedStartTime.text = selectedStartTime
        }, hour, minute, false).show()
    }

    private fun showEndTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY) + 1
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            selectedEndTime = DateTimeUtils.formatTime(selectedHour, selectedMinute)
            tvSelectedEndTime.text = selectedEndTime
        }, hour % 24, minute, false).show()
    }

    private fun updateTask() {
        val title = etTitle.text?.toString()?.trim() ?: ""
        val subject = etSubject.text?.toString()?.trim() ?: ""
        val description = etDescription.text?.toString()?.trim() ?: ""

        var isValid = true

        if (title.isEmpty()) {
            tilTitle.error = getString(R.string.error_title_required)
            isValid = false
        } else {
            tilTitle.error = null
        }

        if (subject.isEmpty()) {
            tilSubject.error = getString(R.string.error_subject_required)
            isValid = false
        } else {
            tilSubject.error = null
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_date_required), Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (!isValid) return

        val selectedPriorityId = rgPriority.checkedRadioButtonId
        val priority = when (selectedPriorityId) {
            R.id.rbPriorityHigh -> "High"
            R.id.rbPriorityLow -> "Low"
            else -> "Medium"
        }

        val hasReminder = switchReminder.isChecked

        val updatedTask = TaskEntity(
            id = taskId,
            title = title,
            subject = subject,
            description = description,
            date = selectedDate,
            startTime = selectedStartTime,
            endTime = selectedEndTime,
            priority = priority,
            completed = isCompleted,
            reminder = hasReminder
        )

        viewModel.update(updatedTask) {
            if (hasReminder) {
                ReminderHelper.scheduleReminder(this, updatedTask)
            } else {
                ReminderHelper.cancelReminder(this, taskId)
            }
            runOnUiThread {
                Toast.makeText(this, getString(R.string.task_updated_success), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
