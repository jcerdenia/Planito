package com.cerdenia.android.planito.data.db

import androidx.room.TypeConverter
import com.cerdenia.android.planito.data.Day
import com.cerdenia.android.planito.data.model.TaskTime
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

    @TypeConverter
    fun fromDaySet(daySet: Set<Day>?): String? = daySet?.joinToString()

    @TypeConverter
    fun toDaySet(days: String?): Set<Day>? = days
        ?.split(", ")
        ?.filterNot { it.isEmpty() }
        ?.map { Day.valueOf(it) }
        ?.toSet()
}