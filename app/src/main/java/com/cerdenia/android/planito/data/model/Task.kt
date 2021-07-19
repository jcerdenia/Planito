package com.cerdenia.android.planito.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cerdenia.android.planito.data.Day
import java.util.*

@Entity
data class Task(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    @ColumnInfo(name = "start_time")
    var startTime: TaskTime = TaskTime(0, 0),
    var endTime: TaskTime = TaskTime(0, 0),
    var days: Set<Day> = Day.values().toSet()
) {

    val duration: TaskTime get() {
        val startMinutes = startTime.toMinutes()
        val endMinutes = endTime.toMinutes()
        val offset = if (endMinutes < startMinutes) 1440 else 0
        return ((endMinutes + offset) - startMinutes).run {
            TaskTime.fromMinutes(this)
        }
    }

    fun setDuration(minutes: Int) {
        endTime = TaskTime.fromMinutes(startTime.toMinutes() + minutes)
    }

    fun sansTimes(): TaskSansTimes = TaskSansTimes(name, description, days)

    fun times(): TaskTimes = TaskTimes(startTime, endTime)
}