package com.cerdenia.android.planito

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cerdenia.android.planito.data.Task
import com.cerdenia.android.planito.databinding.ListItemTaskBinding
import java.util.*

class TaskListAdapter(
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
            binding.startTimeTextView.text = task.startTime.to24hourFormat()
            binding.durationTextView.text = task.duration.to24hourFormat()
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