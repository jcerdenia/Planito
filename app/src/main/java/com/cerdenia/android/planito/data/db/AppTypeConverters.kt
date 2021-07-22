package com.cerdenia.android.planito.data.db

import androidx.room.TypeConverter
import com.cerdenia.android.planito.data.models.Day
import java.util.*

class AppTypeConverters {

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun toUUID(uuid: String?): UUID? = UUID.fromString(uuid)

    @TypeConverter
    fun fromDaySet(daySet: Set<Day>?): String? = daySet?.joinToString()

    @TypeConverter
    fun toDaySet(days: String?): Set<Day>? = days
        ?.split(", ")
        ?.filterNot { it.isEmpty() }
        ?.map { Day.valueOf(it) }
        ?.toSet()
}