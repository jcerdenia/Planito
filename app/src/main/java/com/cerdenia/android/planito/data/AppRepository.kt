package com.cerdenia.android.planito.data

import com.cerdenia.android.planito.data.db.AppDatabase
import com.cerdenia.android.planito.data.model.Task
import java.util.*
import java.util.concurrent.Executors

class AppRepository private constructor(
    db: AppDatabase,
    private val calEditor: CalendarEditor,
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

    fun getUserCalendars(ownerAccount: String) = calEditor.getCalendars(ownerAccount)

    fun syncTasksToCalendar(calendarID: Long, tasks: List<Task>) {
        executor.execute {
            val oldEventIDs = AppPreferences.calendarEventIDs
            calEditor.deleteEvents(oldEventIDs)
            val newEventIDs = calEditor.addEvents(calendarID, tasks)
            AppPreferences.calendarEventIDs = newEventIDs
        }
    }

    companion object {

        private var INSTANCE: AppRepository? = null

        fun init(db: AppDatabase, calEditor: CalendarEditor) {
            INSTANCE = AppRepository(db, calEditor)
        }

        fun getInstance(): AppRepository {
            return INSTANCE ?: throw Exception("AppRepository must be initialized")
        }
    }
}