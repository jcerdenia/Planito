package com.cerdenia.android.planito.ui.tasklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.cerdenia.android.planito.data.AppPreferences
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.model.Task

class TaskListViewModel(
    private val repo: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    private val tasksDbLive = repo.getTasks()
    private val _tasksLive = MediatorLiveData<List<Task>>()
    val tasksLive: LiveData<List<Task>> get() = _tasksLive

    private val tasks get() = tasksLive.value

    val userCalendarName get() = AppPreferences.calendarName

    init {
        _tasksLive.addSource(tasksDbLive) { tasks ->
            _tasksLive.value = tasks.sortedBy { it.startTime.toMinutes() }
        }
    }

    fun addTask(task: Task) {
        repo.addTask(task)
    }

    fun getLatestItem(): Task? {
        return tasksLive.value?.maxByOrNull { it.endTime.toMinutes() }
    }

    fun syncToCalendar() {
        tasks?.let { repo.syncTasksToCalendar(AppPreferences.calendarID, it) }
    }
}