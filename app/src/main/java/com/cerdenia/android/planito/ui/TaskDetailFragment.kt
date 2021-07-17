package com.cerdenia.android.planito.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.model.Task
import com.cerdenia.android.planito.data.model.TaskTime
import com.cerdenia.android.planito.databinding.FragmentTaskDetailBinding
import com.cerdenia.android.planito.extension.toEditable
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
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_task_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_delete -> handleDeleteTask()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleDeleteTask(): Boolean {
        viewModel.deleteCurrentTask()
        callbacks?.onTaskSavedOrDeleted()
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskLive.observe(viewLifecycleOwner, { task ->
            task?.let { updateUI(it) }
        })

        parentFragmentManager.apply {
            setFragmentResultListener(PICK_START_TIME, viewLifecycleOwner, { _, result ->
                handleTimePickerFragmentResult(result) { time ->
                    viewModel.updateStartTime(time)
                }
            })

            setFragmentResultListener(PICK_END_TIME, viewLifecycleOwner, { _, result ->
                handleTimePickerFragmentResult(result) { time ->
                    viewModel.updateEndTime(time) }
            })
        }

        binding.startTimeButton.setOnClickListener {
            viewModel.currentTask?.startTime?.let { time ->
                TimePickerFragment
                    .newInstance(time, PICK_START_TIME)
                    .show(parentFragmentManager, TimePickerFragment.TAG)
            }
        }

        binding.endTimeButton.setOnClickListener {
            viewModel.currentTask?.endTime?.let { time ->
                TimePickerFragment
                    .newInstance(time, PICK_END_TIME)
                    .show(parentFragmentManager, TimePickerFragment.TAG)
            }
        }

        binding.saveButton.setOnClickListener {
            saveUIData()
            callbacks?.onTaskSavedOrDeleted()
        }
    }

    private fun updateUI(task: Task) {
        binding.nameField.text = task.name.toEditable()
        binding.descriptionField.text = task.description.toEditable()
        binding.startTimeButton.text = task.startTime.to12HourFormat()
        binding.endTimeButton.text = task.endTime.to12HourFormat()
    }

    private fun saveUIData() {
        val name = binding.nameField.text.toString()
        val description = binding.descriptionField.text.toString()
        viewModel.saveData(name, description)
    }

    private fun handleTimePickerFragmentResult(
        result: Bundle,
        callback: (TaskTime) -> Unit
    ) {
        val hour = result.getInt(TimePickerFragment.HOUR)
        val minute = result.getInt(TimePickerFragment.MINUTE)
        TaskTime(hour, minute).run { callback(this) }
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

        private const val TAG = "TaskDetailFragment"
        private const val TASK_ID = "task_id"
        private const val PICK_START_TIME = "pick_start_time"
        private const val PICK_END_TIME = "pick_end_time"

        fun newInstance(taskID: UUID): TaskDetailFragment {
            return TaskDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(TASK_ID, taskID.toString())
                }
            }
        }
    }
}