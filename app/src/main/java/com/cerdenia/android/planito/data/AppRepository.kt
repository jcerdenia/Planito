package com.cerdenia.android.planito.data

import com.cerdenia.android.planito.data.db.AppDatabase
import com.cerdenia.android.planito.data.model.Task
import java.util.*
import java.util.concurrent.Executors

class AppRepository private constructor(
    db: AppDatabase,
    private val calendarEditor: CalendarEditor,
) {

    private val dao = db.taskDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun addTask(task: Task) {
        executor.execute { dao.addTask(task) }
    }

    fun getTasks() = dao.getTasks()

    fun getTask(id: UUID) = dao.getTask(id)

    fun updateTask(task: Task) {
        executor.execute { dao.updateTask(task) }
    }

    fun deleteTaskByID(id: UUID) {
        executor.execute { dao.deleteTaskByID(id) }
    }

    fun syncTasksToCalendar(tasks: List<Task>) {
        executor.execute {
            val oldEventIDs = AppPreferences.calendarEventIDs
            calendarEditor.deleteEvents(oldEventIDs)
            val newEventIDs = calendarEditor.addEvents(tasks)
            AppPreferences.calendarEventIDs = newEventIDs
        }
    }

    companion object {

        private var INSTANCE: AppRepository? = null

        fun init(db: AppDatabase, calendarEditor: CalendarEditor) {
            INSTANCE = AppRepository(db, calendarEditor)
        }

        fun getInstance(): AppRepository {
            return INSTANCE ?: throw Exception("AppRepository must be initialized")
        }
    }
}