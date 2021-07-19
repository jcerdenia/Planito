package com.cerdenia.android.planito.data.model

import java.text.DateFormat
import java.util.*

data class TaskTime(
    var hour: Int = 0,
    var minute: Int = 0,
) {

    fun toMinutes(): Int = (hour * 60) + minute

    fun to12HourFormat(): String {
        return Calendar.getInstance()
            .apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            .run {
                DateFormat
                    .getTimeInstance(DateFormat.SHORT)
                    .format(this.time)
            }
    }

    companion object {

        fun fromMinutes(minutes: Int): TaskTime {
            val hour = kotlin.math.floor(minutes / 60.0).toInt()
            val minute = minutes % 60
            return TaskTime(hour, minute)
        }
    }
}