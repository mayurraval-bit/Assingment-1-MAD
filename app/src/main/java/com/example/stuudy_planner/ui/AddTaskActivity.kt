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

class AddTaskActivity : AppCompatActivity() {

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
    private lateinit var switchReminder: MaterialSwitch
    private lateinit var btnSaveTask: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private var selectedDate: String = DateTimeUtils.getTodayDate()
    private var selectedStartTime: String = ""
    private var selectedEndTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        initViews()
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
        switchReminder = findViewById(R.id.switchReminder)
        btnSaveTask = findViewById(R.id.btnSaveTask)
        btnCancel = findViewById(R.id.btnCancel)

        // Set default date to today
        tvSelectedDate.text = DateTimeUtils.formatDisplayDate(selectedDate)
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

        // Save Button
        btnSaveTask.setOnClickListener {
            saveTask()
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

    private fun saveTask() {
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

        val newTask = TaskEntity(
            title = title,
            subject = subject,
            description = description,
            date = selectedDate,
            startTime = selectedStartTime,
            endTime = selectedEndTime,
            priority = priority,
            completed = false,
            reminder = hasReminder
        )

        viewModel.insert(newTask) { generatedId ->
            if (hasReminder) {
                val taskWithId = newTask.copy(id = generatedId)
                ReminderHelper.scheduleReminder(this, taskWithId)
            }
            runOnUiThread {
                Toast.makeText(this, getString(R.string.task_saved_success), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
