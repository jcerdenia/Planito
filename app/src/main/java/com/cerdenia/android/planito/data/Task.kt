package com.cerdenia.android.planito.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Task(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    @ColumnInfo(name = "start_time")
    var startTime: TaskTime = TaskTime(0, 0),
    var endTime: TaskTime = TaskTime(0, 0),
    var recurrence: Int = Recurrence.DAILY.ordinal,
) {

    val duration: TaskTime
        get() = TaskTime.fromMinutes(endTime.toMinutes() - startTime.toMinutes())


    fun setDuration(duration: TaskTime) {
        endTime = TaskTime.fromMinutes(startTime.toMinutes() + duration.toMinutes())
    }

    fun setDuration(minutes: Int) {
        endTime = TaskTime.fromMinutes(startTime.toMinutes() + minutes)
    }
}