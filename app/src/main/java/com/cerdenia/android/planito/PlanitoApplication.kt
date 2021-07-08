package com.cerdenia.android.planito

import android.app.Application
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.db.AppDatabase

class PlanitoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.build(this)
        AppRepository.init(db)
    }
}