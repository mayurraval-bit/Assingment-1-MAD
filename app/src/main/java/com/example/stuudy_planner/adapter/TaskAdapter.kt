package com.example.stuudy_planner.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stuudy_planner.R
import com.example.stuudy_planner.data.TaskEntity
import com.example.stuudy_planner.utils.DateTimeUtils

class TaskAdapter(
    private val onTaskClick: (TaskEntity) -> Unit,
    private val onCompletionToggle: (TaskEntity, Boolean) -> Unit,
    private val onEditClick: (TaskEntity) -> Unit,
    private val onDeleteClick: (TaskEntity) -> Unit
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        private val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cbCompleted)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val tvTimeSeparator: TextView = itemView.findViewById(R.id.tvTimeSeparator)
        private val ivTimeIcon: ImageView = itemView.findViewById(R.id.ivTimeIcon)

        fun bind(task: TaskEntity) {
            tvSubject.text = task.subject
            tvTitle.text = task.title

            // Description
            if (task.description.isNotBlank()) {
                tvDescription.text = task.description
                tvDescription.visibility = View.VISIBLE
            } else {
                tvDescription.visibility = View.GONE
            }

            // Date
            tvDate.text = DateTimeUtils.formatDisplayDate(task.date)

            // Time
            val timeText = when {
                task.startTime.isNotBlank() && task.endTime.isNotBlank() -> "${task.startTime} - ${task.endTime}"
                task.startTime.isNotBlank() -> task.startTime
                task.endTime.isNotBlank() -> task.endTime
                else -> ""
            }

            if (timeText.isNotBlank()) {
                tvTime.text = timeText
                tvTime.visibility = View.VISIBLE
                ivTimeIcon.visibility = View.VISIBLE
                tvTimeSeparator.visibility = View.VISIBLE
            } else {
                tvTime.visibility = View.GONE
                ivTimeIcon.visibility = View.GONE
                tvTimeSeparator.visibility = View.GONE
            }

            // Priority Badge Styling
            when (task.priority.lowercase()) {
                "high" -> {
                    tvPriority.text = "High"
                    tvPriority.setBackgroundResource(R.drawable.bg_priority_high)
                    tvPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.priority_high))
                }
                "medium" -> {
                    tvPriority.text = "Medium"
                    tvPriority.setBackgroundResource(R.drawable.bg_priority_medium)
                    tvPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.priority_medium))
                }
                else -> {
                    tvPriority.text = "Low"
                    tvPriority.setBackgroundResource(R.drawable.bg_priority_low)
                    tvPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.priority_low))
                }
            }

            // Completion state
            // Uncheck listener first to prevent recursive triggers during binding
            cbCompleted.setOnCheckedChangeListener(null)
            cbCompleted.isChecked = task.completed

            if (task.completed) {
                tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvTitle.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_muted))
            } else {
                tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvTitle.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_primary))
            }

            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                onCompletionToggle(task, isChecked)
            }

            // Clicks
            itemView.setOnClickListener {
                onTaskClick(task)
            }

            btnEdit.setOnClickListener {
                onEditClick(task)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(task)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}
