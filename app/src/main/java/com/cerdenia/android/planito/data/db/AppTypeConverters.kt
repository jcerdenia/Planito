package com.cerdenia.android.planito.data.db

import androidx.room.TypeConverter
import com.cerdenia.android.planito.data.TaskTime
import java.util.*

class AppTypeConverters {

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun toUUID(uuid: String?): UUID? = UUID.fromString(uuid)

    @TypeConverter
    fun fromTaskTime(taskTime: TaskTime?): Int? = taskTime?.toMinutes()

    @TypeConverter
    fun toTaskTime(minutes: Int?) = minutes?.let { TaskTime.fromMinutes(it) }
}