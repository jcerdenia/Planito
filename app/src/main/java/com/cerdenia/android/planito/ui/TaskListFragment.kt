package com.cerdenia.android.planito.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerdenia.android.planito.TaskListAdapter
import com.cerdenia.android.planito.data.Task
import com.cerdenia.android.planito.data.TaskTime
import com.cerdenia.android.planito.databinding.FragmentTaskListBinding
import java.util.*

class TaskListFragment : Fragment(), TaskListAdapter.Listener {

    interface Callbacks {

        fun onTaskSelected(taskID: UUID)
    }

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var adapter: TaskListAdapter
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
        adapter = TaskListAdapter(this@TaskListFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.tasksLive.observe(viewLifecycleOwner, { tasks ->
            adapter.submitList(tasks.sortedBy { it.endTime.toMinutes() })
        })

        binding.fab.setOnClickListener {
            val newTask = Task()
            newTask.startTime = viewModel.getLatestItem()?.endTime ?: TaskTime()
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

        fun newInstance(): TaskListFragment = TaskListFragment()
    }
}