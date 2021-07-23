package com.cerdenia.android.planito.ui.tasklist

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.models.Task
import com.cerdenia.android.planito.databinding.FragmentTaskListBinding
import com.cerdenia.android.planito.interfaces.OnFragmentLoaded
import com.cerdenia.android.planito.ui.dialogs.AlertFragment
import com.cerdenia.android.planito.ui.dialogs.ConfirmSyncFragment
import com.cerdenia.android.planito.ui.dialogs.NewTaskFragment
import com.cerdenia.android.planito.utils.CalendarPermissions
import java.util.*

class TaskListFragment : Fragment(), TaskListAdapter.Listener {

    interface Callbacks: OnFragmentLoaded {

        fun onTaskSelected(taskID: UUID)

        fun onTaskSettingsClicked()
    }

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var adapter: TaskListAdapter
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
        adapter = TaskListAdapter(context.resources, this)
        CalendarPermissions.setResultWhenGranted(this, ::syncToCalendar)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        callbacks?.onFragmentLoaded(TAG)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.tasksLive.observe(viewLifecycleOwner, { tasks ->
            adapter.submitList(tasks)
        })

        parentFragmentManager.setFragmentResultListener(
            REQUEST_ADD_TASK,
            viewLifecycleOwner,
            { _, result ->
                val newTask = Task().apply {
                    name = result.getString(NewTaskFragment.TASK_NAME) ?: getString(R.string.new_task)
                    startMinutes = viewModel.getLatestItem()?.endMinutes ?: 0
                    setDuration(60)
                }

                viewModel.addTask(newTask)
                callbacks?.onTaskSelected(newTask.id)
            }
        )

        parentFragmentManager.setFragmentResultListener(
            REQUEST_CONFIRM_SYNC,
            viewLifecycleOwner,
            { _, _ ->
                val isPermitted = CalendarPermissions.isAlreadyGranted(context)
                if (isPermitted) syncToCalendar() else CalendarPermissions.request()
            }
        )
    }

    override fun onStart() {
        super.onStart()

        binding.fab.setOnClickListener {
            NewTaskFragment
                .newInstance(REQUEST_ADD_TASK)
                .show(parentFragmentManager, NewTaskFragment.TAG)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_task_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_sync -> onSyncMenuItemSelected()
            R.id.menu_item_settings -> onSettingsMenuItemSelected()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onSyncMenuItemSelected(): Boolean {
        ConfirmSyncFragment
            .newInstance(REQUEST_CONFIRM_SYNC, viewModel.userCalendarName)
            .show(parentFragmentManager, ConfirmSyncFragment.TAG)
        return true
    }

    private fun onSettingsMenuItemSelected(): Boolean {
        callbacks?.onTaskSettingsClicked()
        return true
    }

    private fun syncToCalendar() {
        viewModel.syncToCalendar()
        AlertFragment
            .newInstance(R.string.synced, R.string.synced_message, R.drawable.ic_success)
            .show(parentFragmentManager, ConfirmSyncFragment.TAG)
    }

    override fun onTaskSelected(taskID: UUID) {
        callbacks?.onTaskSelected(taskID)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        CalendarPermissions.cleanup()
        callbacks = null
    }

    companion object {

        const val TAG = "TaskListFragment"

        private const val REQUEST_CONFIRM_SYNC = "request_confirm_sync"
        private const val REQUEST_ADD_TASK = "request_add_task"

        fun newInstance(): TaskListFragment = TaskListFragment()
    }
}