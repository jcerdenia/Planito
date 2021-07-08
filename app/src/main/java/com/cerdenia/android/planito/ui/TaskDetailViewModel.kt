package com.cerdenia.android.planito.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.Task
import com.cerdenia.android.planito.data.TaskTime
import java.util.*

class TaskDetailViewModel(
    private val repo: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    private val taskIDLive = MutableLiveData<UUID>()

    val taskLive: LiveData<Task?> = Transformations.switchMap(taskIDLive) { taskID ->
        repo.getTask(taskID)
    }
    
    private val currentTask get () = taskLive.value

    fun fetchTask(id: UUID) {
        taskIDLive.value = id
    }
    
    fun saveData(name: String, description: String, startTime: TaskTime, duration: TaskTime) {
        currentTask?.let { task ->
            task.name = name
            task.description = description
            task.startTime = startTime
            task.duration = duration
            repo.updateTask(task)
        }
    }
}