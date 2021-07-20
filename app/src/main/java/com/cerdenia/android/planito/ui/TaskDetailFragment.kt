package com.cerdenia.android.planito.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.Day
import com.cerdenia.android.planito.data.model.Task
import com.cerdenia.android.planito.databinding.FragmentTaskDetailBinding
import com.cerdenia.android.planito.extension.getDayCheckBoxes
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

    private lateinit var dayCheckBoxes: List<CheckBox>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

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
        dayCheckBoxes = binding.getDayCheckBoxes()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskLive.observe(viewLifecycleOwner, { task ->
            task?.let { updateUI(it) }
        })


        parentFragmentManager.apply {
            setFragmentResultListener(PICK_START_TIME, viewLifecycleOwner, { _, result ->
                val time = TimePickerFragment.unbundleFragmentResult(result)
                binding.startTimeButton.text = time.to12HourFormat()
                viewModel.onTaskStartTimeChanged(time)
            })

            setFragmentResultListener(PICK_END_TIME, viewLifecycleOwner, { _, result ->
                val time = TimePickerFragment.unbundleFragmentResult(result)
                binding.endTimeButton.text = time.to12HourFormat()
                viewModel.onTaskEndTimeChanged(time)
            })
        }

        binding.startTimeButton.setOnClickListener {
            TimePickerFragment
                .newInstance(viewModel.taskStart, PICK_START_TIME)
                .show(parentFragmentManager, TimePickerFragment.TAG)
        }

        binding.endTimeButton.setOnClickListener {
            TimePickerFragment
                .newInstance(viewModel.taskEnd, PICK_END_TIME)
                .show(parentFragmentManager, TimePickerFragment.TAG)
        }

        binding.saveButton.setOnClickListener {
            updateTaskDetails()
            viewModel.saveChanges()
            callbacks?.onTaskSavedOrDeleted()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_task_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_delete -> {
                viewModel.deleteCurrentTask()
                callbacks?.onTaskSavedOrDeleted()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(task: Task) {
        binding.nameField.text = task.name.toEditable()
        binding.descriptionField.text = task.description.toEditable()
        binding.startTimeButton.text = task.startTime.to12HourFormat()
        binding.endTimeButton.text = task.endTime.to12HourFormat()

        dayCheckBoxes.forEachIndexed { i, checkBox ->
            checkBox.isChecked = task.days.contains(Day.list[i])
        }
    }

    private fun updateTaskDetails() {
        val name = binding.nameField.text.toString()
        val description = binding.descriptionField.text.toString()
        val days = dayCheckBoxes
            .mapIndexed { i, checkBox -> Pair(i, checkBox.isChecked) }
            .filter { it.second } // it.second == true
            .map { Day.list[it.first] }
            .toSet()

        viewModel.updateTaskDetails(name, description, days)
    }

    override fun onDestroyView() {
        updateTaskDetails()
        _binding = null
        super.onDestroyView()
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