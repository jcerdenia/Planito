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
    var duration: TaskTime = TaskTime(1, 0),
    var recurrence: Int = Recurrence.DAILY.ordinal,
) {

    val endTime: TaskTime
        get() = (startTime.toMinutes() + duration.toMinutes()).run {
            TaskTime.fromMinutes(this)
        }
}