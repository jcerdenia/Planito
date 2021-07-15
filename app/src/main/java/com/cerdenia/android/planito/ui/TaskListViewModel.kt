package com.cerdenia.android.planito.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.Task

class TaskListViewModel(
    private val repo: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    private val tasksDbLive = repo.getTasks()
    private val _tasksLive = MediatorLiveData<List<Task>>()
    val tasksLive: LiveData<List<Task>> get() = _tasksLive

    init {
        _tasksLive.addSource(tasksDbLive) { tasks ->
            _tasksLive.value = tasks.sortedBy { it.endTime.toMinutes() }
        }
    }

    fun addTask(task: Task) {
        repo.addTask(task)
    }

    fun getLatestItem(): Task? {
        return tasksLive.value?.maxByOrNull { it.endTime.toMinutes() }
    }
}