package com.cerdenia.android.planito.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cerdenia.android.planito.data.Day
import java.util.*

@Entity
data class Task(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    @ColumnInfo(name = "start_minutes") var startMinutes: Int = 0,
    @ColumnInfo(name = "end_minutes") var endMinutes: Int = 0,
    var days: Set<Day> = Day.values().toSet()
) {

    val startTime: TaskTime get() = TaskTime.fromMinutes(startMinutes)

    val endTime: TaskTime get() = TaskTime.fromMinutes(endMinutes)

    val duration: TaskTime get() {
        val dayOffset = if (endMinutes < startMinutes) 1440 else 0
        return ((endMinutes + dayOffset) - startMinutes).run {
            TaskTime.fromMinutes(this)
        }
    }

    fun setDuration(minutes: Int) {
        endMinutes = startMinutes + minutes
    }
}