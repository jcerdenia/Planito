package com.cerdenia.android.planito.ui

import androidx.lifecycle.*
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.Day
import com.cerdenia.android.planito.data.model.Task
import com.cerdenia.android.planito.data.model.TaskSansTimes
import com.cerdenia.android.planito.data.model.TaskTime
import com.cerdenia.android.planito.data.model.TaskTimes
import java.util.*

class TaskDetailViewModel(
    private val repo: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    private val taskIDLive = MutableLiveData<UUID>()

    private val taskLive: LiveData<Task?> = Transformations
        .switchMap(taskIDLive) { taskID -> repo.getTask(taskID) }

    val currentTask get() = taskLive.value

    private val _taskSansTimes = MediatorLiveData<TaskSansTimes>()
    val taskSansTimes: LiveData<TaskSansTimes> get() = _taskSansTimes

    private val _taskTimes = MediatorLiveData<TaskTimes>()
    val taskTimes: LiveData<TaskTimes> = _taskTimes

    init {
        _taskSansTimes.addSource(taskLive) { task ->
            _taskSansTimes.value = task?.sansTimes()
        }

        _taskTimes.addSource(taskLive) { task ->
            _taskTimes.value = task?.times()
        }
    }

    fun fetchTask(id: UUID) {
        taskIDLive.value = id
    }

    fun updateTaskStartTime(time: TaskTime) {
        _taskTimes.value = _taskTimes.value?.apply {
            start = time
        }
    }

    fun updateTaskEndTime(time: TaskTime) {
        _taskTimes.value = _taskTimes.value?.apply {
            end = time
        }
    }

    fun updateTaskDetails(name: String, description: String, days: Set<Day>) {
        _taskSansTimes.value = TaskSansTimes(name, description, days)
    }

    fun confirmChanges() {
        currentTask?.let { task ->
            _taskSansTimes.value?.let {
                task.name = it.name
                task.description = it.description
                task.days = it.days
            }

            _taskTimes.value?.let {
                task.startTime = it.start
                task.endTime = it.end
            }

            repo.updateTask(task)
        }
    }

    fun deleteCurrentTask() {
        currentTask?.let { task ->
            repo.deleteTaskByID(task.id)
        }
    }
}