package com.cerdenia.android.planito.data

import com.cerdenia.android.planito.data.db.AppDatabase
import java.util.*
import java.util.concurrent.Executors

class AppRepository private constructor(db: AppDatabase) {

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

    companion object {

        private var INSTANCE: AppRepository? = null

        fun init(db: AppDatabase) {
            INSTANCE = AppRepository(db)
        }

        fun getInstance(): AppRepository {
            return INSTANCE ?: throw Exception("AppRepository must be initialized")
        }
    }
}