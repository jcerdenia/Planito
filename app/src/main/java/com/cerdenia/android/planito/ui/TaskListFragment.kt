package com.cerdenia.android.planito.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.CalendarEditor
import com.cerdenia.android.planito.data.model.Task
import com.cerdenia.android.planito.data.model.TaskTime
import com.cerdenia.android.planito.databinding.FragmentTaskListBinding
import com.cerdenia.android.planito.util.CalendarPermissions
import java.util.*

class TaskListFragment : Fragment(),
    TaskListAdapter.Listener,
    CalendarPermissions.Launcher {

    interface Callbacks {

        fun onTaskSelected(taskID: UUID)
    }

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var adapter: TaskListAdapter
    private var callbacks: Callbacks? = null

    override val calendarPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.all { it.value == true }
        if (isGranted) viewModel.syncToCalendar()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
        adapter = TaskListAdapter(context.resources, this@TaskListFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_task_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_sync -> handleSync()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleSync(): Boolean {
        // TODO: sync tasks with calendar
        if (CalendarPermissions.isGranted(requireContext())) {
            viewModel.syncToCalendar()
        } else {
            calendarPermissionLauncher.launch(CalendarPermissions.list)
        }

        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.tasksLive.observe(viewLifecycleOwner, { tasks ->
            adapter.submitList(tasks)
        })

        binding.fab.setOnClickListener {
            val newTask = Task().apply {
                startTime = viewModel.getLatestItem()?.endTime ?: TaskTime()
                setDuration(60)
            }

            viewModel.addTask(newTask)
            callbacks?.onTaskSelected(newTask.id)
        }
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
        callbacks = null
    }

    companion object {

        private const val TAG = "TaskListFragment"

        fun newInstance(): TaskListFragment = TaskListFragment()
    }
}