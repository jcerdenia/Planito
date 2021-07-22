package com.cerdenia.android.planito.ui.tasklist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.models.Task
import com.cerdenia.android.planito.databinding.FragmentTaskListBinding
import com.cerdenia.android.planito.interfaces.OnFragmentLoaded
import com.cerdenia.android.planito.ui.dialogs.ConfirmSyncFragment
import com.cerdenia.android.planito.ui.dialogs.NewTaskFragment
import com.cerdenia.android.planito.utils.CalendarPermissions
import java.util.*

class TaskListFragment : Fragment(), TaskListAdapter.Listener {

    interface Callbacks: OnFragmentLoaded {

        fun onTaskSelected(taskID: UUID, isNew: Boolean = false)

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
        CalendarPermissions.setResultIfGranted(this) {
            viewModel.syncToCalendar()
        }
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
            NewTaskFragment.ADD_TASK,
            viewLifecycleOwner,
            { _, result ->
                val newTask = Task().apply {
                    name = result.getString(NewTaskFragment.TASK_NAME) ?: getString(R.string.new_task)
                    startMinutes = viewModel.getLatestItem()?.endMinutes ?: 0
                    setDuration(60)
                }

                viewModel.addTask(newTask)
                callbacks?.onTaskSelected(newTask.id, true)
            }
        )

        parentFragmentManager.setFragmentResultListener(
            ConfirmSyncFragment.CONFIRM_SYNC,
            viewLifecycleOwner,
            { _, _ ->
                val isPermitted = CalendarPermissions.isGranted(context)
                if (isPermitted) viewModel.syncToCalendar() else CalendarPermissions.request()
            }
        )
    }

    override fun onStart() {
        super.onStart()

        binding.fab.setOnClickListener {
            NewTaskFragment
                .newInstance()
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
            .newInstance(viewModel.userCalendarName)
            .show(parentFragmentManager, ConfirmSyncFragment.TAG)
        return true
    }

    private fun onSettingsMenuItemSelected(): Boolean {
        callbacks?.onTaskSettingsClicked()
        return true
    }

    override fun onTaskSelected(taskID: UUID) {
        callbacks?.onTaskSelected(taskID)
    }

    override fun onTaskListChanged() {
        Log.d(TAG, "Task list changed!")
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

        fun newInstance(): TaskListFragment = TaskListFragment()
    }
}