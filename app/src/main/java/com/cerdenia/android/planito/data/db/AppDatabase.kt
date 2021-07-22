package com.cerdenia.android.planito.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cerdenia.android.planito.data.models.Task

@Database(
    entities = [Task::class],
    version = 1
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        private const val NAME = "database"

        fun build(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, NAME)
                .build()
        }
    }
}