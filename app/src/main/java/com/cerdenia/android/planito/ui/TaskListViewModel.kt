package com.cerdenia.android.planito.ui

import androidx.lifecycle.ViewModel
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.Task

class TaskListViewModel(
    private val repo: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    val tasksLive = repo.getTasks()

    fun addTask(task: Task) {
        repo.addTask(task)
    }

    fun getLatestItem(): Task? {
        return tasksLive.value?.maxByOrNull { it.endTime.toMinutes() }
    }
}