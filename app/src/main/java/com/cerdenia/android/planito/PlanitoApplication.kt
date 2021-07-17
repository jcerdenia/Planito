package com.cerdenia.android.planito

import android.app.Application
import com.cerdenia.android.planito.data.AppPreferences
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.CalendarEditor
import com.cerdenia.android.planito.data.db.AppDatabase

class PlanitoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
        val db = AppDatabase.build(this)
        val calendarWriter = CalendarEditor(this)
        AppRepository.init(db, calendarWriter)
    }
}