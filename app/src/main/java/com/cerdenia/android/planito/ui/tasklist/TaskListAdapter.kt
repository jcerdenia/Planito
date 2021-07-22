package com.cerdenia.android.planito.ui.tasklist

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.model.Task
import com.cerdenia.android.planito.databinding.ListItemTaskBinding
import java.util.*

class TaskListAdapter(
    private val resources: Resources,
    private val listener: Listener
): ListAdapter<Task, TaskListAdapter.TaskHolder>(DiffCallback()) {

    interface Listener {

        fun onTaskSelected(taskID: UUID)

        fun onTaskListChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        return TaskHolder(LayoutInflater.from(parent.context).run {
            ListItemTaskBinding.inflate(this)
        })
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        getItem(position).run { holder.bind(this) }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<Task>,
        currentList: MutableList<Task>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        listener.onTaskListChanged()
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

            val (hour, minute) = task.duration
            binding.durationTextView.text = when {
                hour + minute == 0 -> null
                hour == 0 -> resources.getString(
                    R.string.duration_hours_or_minutes,
                    resources.getQuantityString(R.plurals.minutes, minute, minute)
                )
                minute == 0 -> resources.getString(
                    R.string.duration_hours_or_minutes,
                    resources.getQuantityString(R.plurals.hours, hour, hour)
                )
                else -> resources.getString(
                    R.string.duration_hours_and_minutes,
                    resources.getQuantityString(R.plurals.hours, hour, hour),
                    resources.getQuantityString(R.plurals.minutes, minute, minute)
                )
            }

            binding.daysTextView.text = when {
                task.days.size == 5 && task.days.all { it.isWeekday } -> {
                    resources.getString(R.string.weekdays)
                }
                task.days.size == 2 && task.days.all { !it.isWeekday } -> {
                    resources.getString(R.string.weekends)
                }
                task.days.size == 7 -> resources.getString(R.string.daily)
                else -> task.days
                    .toList()
                    .sortedBy { it.orderFromSunday }
                    .joinToString { it.getName(resources).substring(0, 3) }
            }
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
}