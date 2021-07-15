package com.cerdenia.android.planito.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cerdenia.android.planito.data.Task
import com.cerdenia.android.planito.data.TaskTime
import com.cerdenia.android.planito.databinding.FragmentTaskDetailBinding
import com.cerdenia.android.planito.extension.toEditable
import com.cerdenia.android.planito.extension.toInt
import java.util.*

class TaskDetailFragment : Fragment() {

    interface Callbacks {

        fun onTaskSavedOrDeleted()
    }

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels()
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(TASK_ID)
            ?.run { UUID.fromString(this) }
            ?.run { viewModel.fetchTask(this) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskLive.observe(viewLifecycleOwner, { task ->
            task?.let { updateUI(it) }
        })

        binding.deleteButton.setOnClickListener {
            viewModel.deleteCurrentTask()
            callbacks?.onTaskSavedOrDeleted()
        }

        binding.saveButton.setOnClickListener {
            saveUIData()
            callbacks?.onTaskSavedOrDeleted()
        }
    }

    private fun updateUI(task: Task) {
        binding.nameEditText.text = task.name.toEditable()
        binding.descriptionEditText.text = task.description.toEditable()
        binding.startTimePicker.hour = task.startTime.hour
        binding.startTimePicker.minute = task.startTime.minute
        binding.durationHourEditText.text = task.duration.hour.toString().toEditable()
        binding.durationMinuteEditText.text = task.duration.minute.toString().toEditable()
    }

    private fun saveUIData() {
        val name = binding.nameEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val startTimeHour = binding.startTimePicker.hour
        val startTimeMinute = binding.startTimePicker.minute
        val startTime = TaskTime(startTimeHour, startTimeMinute)
        val durationHour = binding.durationHourEditText.text.toInt()
        val durationMinute = binding.durationMinuteEditText.text.toInt()
        val duration = TaskTime(durationHour, durationMinute)
        viewModel.saveData(name, description, startTime, duration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    companion object {

        private const val TASK_ID = "task_id"

        fun newInstance(taskID: UUID): TaskDetailFragment {
            return TaskDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(TASK_ID, taskID.toString())
                }
            }
        }
    }
}