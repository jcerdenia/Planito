package com.cerdenia.android.planito.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.model.Task
import com.cerdenia.android.planito.databinding.FragmentTaskListBinding
import com.cerdenia.android.planito.util.CalendarPermissions
import java.util.*

class TaskListFragment : Fragment(), TaskListAdapter.Listener {

    interface Callbacks {

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
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.tasksLive.observe(viewLifecycleOwner, { tasks ->
            adapter.submitList(tasks)
        })

        binding.fab.setOnClickListener {
            val newTask = Task().apply {
                startMinutes = viewModel.getLatestItem()?.endMinutes ?: 0
                setDuration(60)
            }

            viewModel.addTask(newTask)
            callbacks?.onTaskSelected(newTask.id)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_task_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_sync -> handleSync()
            R.id.menu_item_settings -> handleOpenSettings()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleSync(): Boolean {
        if (CalendarPermissions.isGranted(context)) {
            viewModel.syncToCalendar()
        } else {
            CalendarPermissions.request()
        }

        return true
    }

    private fun handleOpenSettings(): Boolean {
        callbacks?.onTaskSettingsClicked()
        return true
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

        private const val TAG = "TaskListFragment"

        fun newInstance(): TaskListFragment = TaskListFragment()
    }
}