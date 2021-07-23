package com.cerdenia.android.planito.ui.taskdetail

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.models.Task
import com.cerdenia.android.planito.databinding.FragmentTaskDetailBinding
import com.cerdenia.android.planito.extensions.toEditable
import com.cerdenia.android.planito.interfaces.CustomBackPress
import com.cerdenia.android.planito.interfaces.OnFinished
import com.cerdenia.android.planito.interfaces.OnFragmentLoaded
import com.cerdenia.android.planito.ui.dialogs.AlertFragment
import com.cerdenia.android.planito.ui.dialogs.DeleteTaskFragment
import com.cerdenia.android.planito.ui.dialogs.SaveTaskFragment
import com.cerdenia.android.planito.ui.dialogs.TimePickerFragment
import com.cerdenia.android.planito.utils.OnTextChangedListener
import java.util.*

class TaskDetailFragment : Fragment(), CustomBackPress {

    interface Callbacks : OnFragmentLoaded, OnFinished

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels()
    private var callbacks: Callbacks? = null
    private lateinit var dayCheckBoxes: DayCheckBoxesUtil

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
        dayCheckBoxes = DayCheckBoxesUtil(requireContext(), binding)
        callbacks?.onFragmentLoaded(TAG)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskLive.observe(viewLifecycleOwner, { task ->
            task?.let { updateUI(it) }
        })

        parentFragmentManager.setFragmentResultListener(
            REQUEST_PICK_START_TIME,
            viewLifecycleOwner,
            { _, result ->
                val time = TimePickerFragment.unbundleFragmentResult(result)
                binding.startTimeButton.text = time.to12HourFormat()
                viewModel.onTaskStartTimeChanged(time)
            }
        )

        parentFragmentManager.setFragmentResultListener(
            REQUEST_PICK_END_TIME,
            viewLifecycleOwner,
            { _, result ->
                val time = TimePickerFragment.unbundleFragmentResult(result)
                binding.endTimeButton.text = time.to12HourFormat()
                viewModel.onTaskEndTimeChanged(time)
            }
        )

        parentFragmentManager.setFragmentResultListener(
            REQUEST_SAVE_CHANGES,
            viewLifecycleOwner,
            { _, result ->
                when {
                    !result.getBoolean(SaveTaskFragment.IS_POSITIVE) -> callbacks?.onFinished()
                    binding.saveButton.isEnabled -> saveTaskAndFinish()
                    else -> AlertFragment
                        .newInstance(R.string.unable_to_save, R.string.unable_to_save_message)
                        .show(parentFragmentManager, AlertFragment.TAG)
                }
            }
        )

        parentFragmentManager.setFragmentResultListener(
            REQUEST_DELETE,
            viewLifecycleOwner,
            { _, _ -> deleteTaskAndFinish() }
        )
    }

    override fun onStart() {
        super.onStart()

        binding.nameField.addTextChangedListener(OnTextChangedListener { text ->
            binding.saveButton.isEnabled = text.isNotBlank() && dayCheckBoxes.isAnyChecked()
        })

        binding.startTimeButton.setOnClickListener {
            TimePickerFragment
                .newInstance(REQUEST_PICK_START_TIME, viewModel.taskStart)
                .show(parentFragmentManager, TimePickerFragment.TAG)
        }

        binding.endTimeButton.setOnClickListener {
            TimePickerFragment
                .newInstance(REQUEST_PICK_END_TIME, viewModel.taskEnd)
                .show(parentFragmentManager, TimePickerFragment.TAG)
        }

        dayCheckBoxes.setIsAnyCheckedListener { isAnyChecked ->
            binding.saveButton.isEnabled = isAnyChecked && binding.nameField.text.isNotBlank()
        }

        binding.saveButton.setOnClickListener {
            saveTaskAndFinish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_task_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_delete -> handleDeleteItemSelected()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(task: Task) {
        binding.nameField.text = task.name.toEditable()
        binding.descriptionField.text = task.description.toEditable()
        binding.startTimeButton.text = task.startTime.to12HourFormat()
        binding.endTimeButton.text = task.endTime.to12HourFormat()
        dayCheckBoxes.setSelections(task.days)
    }

    private fun updateTaskDetails() {
        val name = binding.nameField.text.toString()
        val description = binding.descriptionField.text.toString()
        val days = dayCheckBoxes.getSelectedDays()
        viewModel.updateTaskDetails(name, description, days)
    }

    private fun handleDeleteItemSelected(): Boolean {
        DeleteTaskFragment
            .newInstance(REQUEST_DELETE)
            .show(parentFragmentManager, DeleteTaskFragment.TAG)
        return true
    }

    private fun saveTaskAndFinish() {
        updateTaskDetails()
        viewModel.saveChanges()
        callbacks?.onFinished()
    }

    private fun deleteTaskAndFinish() {
        viewModel.deleteCurrentTask()
        callbacks?.onFinished()
    }

    override fun onBackPressed() {
        updateTaskDetails()
        if (viewModel.isTaskChanged()) {
            SaveTaskFragment
                .newInstance(REQUEST_SAVE_CHANGES)
                .show(parentFragmentManager, SaveTaskFragment.TAG)
        } else {
            callbacks?.onFinished()
        }
    }

    override fun onPause() {
        super.onPause()
        updateTaskDetails()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    companion object {

        const val TAG = "TaskDetailFragment"
        private const val TASK_ID = "task_id"

        // Dialog fragment request keys
        private const val REQUEST_PICK_START_TIME = "request_pick_start_time"
        private const val REQUEST_PICK_END_TIME = "request_pick_end_time"
        private const val REQUEST_SAVE_CHANGES = "request_save_changes"
        private const val REQUEST_DELETE = "request_delete"

        fun newInstance(taskID: UUID): TaskDetailFragment {
            return TaskDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(TASK_ID, taskID.toString())
                }
            }
        }
    }
}