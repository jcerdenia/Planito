package com.cerdenia.android.planito.ui.taskdetail

import androidx.lifecycle.*
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.model.Day
import com.cerdenia.android.planito.data.model.Task
import com.cerdenia.android.planito.data.model.TaskTime
import java.util.*

class TaskDetailViewModel(
    private val repo: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    private val taskIDLive = MutableLiveData<UUID>()
    private val taskDbLive: LiveData<Task?> = Transformations
        .switchMap(taskIDLive) { taskID -> repo.getTask(taskID) }

    private val _taskLive = MediatorLiveData<Task?>()
    val taskLive: LiveData<Task?> get() = _taskLive
    private val currentTask get() = taskDbLive.value

    var taskName = ""
        private set
    private var taskDescription = ""
    var taskStart = TaskTime(0, 0)
        private set
    var taskEnd = TaskTime(0, 0)
        private set
    private var taskDays = setOf<Day>()
    var isFirstTimeLoading = true
        private set

    init {
        _taskLive.addSource(taskDbLive) { task ->
            _taskLive.value = if (isFirstTimeLoading) {
                task?.let { onTaskFirstTimeLoaded(it) }
                task
            } else {
                getTaskWithUnsavedChanges()
            }
        }
    }

    fun fetchTask(id: UUID) {
        taskIDLive.value = id
    }

    private fun onTaskFirstTimeLoaded(task: Task) {
        taskName = task.name
        taskDescription = task.description
        taskStart = task.startTime
        taskEnd = task.endTime
        taskDays = task.days
        isFirstTimeLoading = false
    }

    fun updateTaskDetails(name: String, description: String, days: Set<Day>) {
        taskName = name
        taskDescription = description
        taskDays = days
    }

    fun onTaskStartTimeChanged(time: TaskTime) {
        taskStart = time
    }

    fun onTaskEndTimeChanged(time: TaskTime) {
        taskEnd = time
    }

    private fun getTaskWithUnsavedChanges(): Task = Task(
        name = taskName,
        description = taskDescription,
        startMinutes = taskStart.toMinutes(),
        endMinutes = taskEnd.toMinutes(),
        days = taskDays
    )

    fun saveChanges() {
        currentTask?.let { task ->
            task.name = taskName
            task.description = taskDescription
            task.startMinutes = taskStart.toMinutes()
            task.endMinutes = taskEnd.toMinutes()
            task.days = taskDays
            repo.updateTask(task)
        }
    }

    fun deleteCurrentTask() {
        currentTask?.let { task ->
            repo.deleteTaskByID(task.id)
        }
    }
}