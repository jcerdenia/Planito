package com.cerdenia.android.planito.ui

import androidx.lifecycle.*
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.model.Task
import com.cerdenia.android.planito.data.model.TaskTime
import java.util.*

class TaskDetailViewModel(
    private val repo: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    private val taskIDLive = MutableLiveData<UUID>()

    private val taskDbLive: LiveData<Task?> = Transformations
        .switchMap(taskIDLive) { taskID -> repo.getTask(taskID) }

    val taskLive = MediatorLiveData<Task?>()

    val currentTask get () = taskLive.value

    init {
        taskLive.addSource(taskDbLive) { task ->
            taskLive.value = task
        }
    }

    fun fetchTask(id: UUID) {
        taskIDLive.value = id
    }
    
    fun saveData(name: String, description: String) {
        currentTask?.let { task ->
            task.name = name
            task.description = description
            repo.updateTask(task)
        }
    }

    fun updateStartTime(time: TaskTime) {
        taskLive.value = currentTask?.apply {
            startTime = time
        }
    }

    fun updateEndTime(time: TaskTime) {
        taskLive.value = currentTask?.apply {
            endTime = time
        }
    }

    fun deleteCurrentTask() {
        currentTask?.let { task ->
            repo.deleteTaskByID(task.id)
        }
    }
}