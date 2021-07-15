package com.cerdenia.android.planito.ui

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.Task
import com.cerdenia.android.planito.databinding.ListItemTaskBinding
import java.util.*

class TaskListAdapter(
    private val resources: Resources,
    private val listener: Listener
): ListAdapter<Task, TaskListAdapter.TaskHolder>(DiffCallback()) {

    interface Listener {

        fun onTaskSelected(taskID: UUID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        return TaskHolder(LayoutInflater.from(parent.context).run {
            ListItemTaskBinding.inflate(this)
        })
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        getItem(position).run { holder.bind(this) }
    }

    inner class TaskHolder(
        private val binding: ListItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        private lateinit var taskID: UUID

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(task: Task) {
            taskID = task.id
            binding.nameTextView.text = task.name

            binding.timeRangeTextView.text = resources.getString(
                R.string.time_range,
                task.startTime.to12HourFormat(),
                task.endTime.to12HourFormat()
            )

            binding.durationTextView.text = resources.getString(
                R.string.duration_text,
                resources.getQuantityString(R.plurals.hours, task.duration.hour, task.duration.hour),
                resources.getQuantityString(R.plurals.minutes, task.duration.minute, task.duration.minute)
            )
        }

        override fun onClick(p0: View?) {
            listener.onTaskSelected(taskID)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

    companion object {

        private const val TAG = "TaskListAdapter"
    }
}