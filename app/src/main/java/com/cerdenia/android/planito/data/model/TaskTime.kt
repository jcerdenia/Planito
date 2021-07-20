package com.cerdenia.android.planito.data.model

import java.text.DateFormat
import java.util.*

data class TaskTime(
    var hour: Int = 0,
    var minute: Int = 0,
) {

    fun toMinutes(): Int = (hour * 60) + minute

    fun toMillis(): Long {
        return Calendar.getInstance().run {
            set(Calendar.HOUR_OF_DAY,hour)
            set(Calendar.MINUTE, minute)
            timeInMillis
        }
    }

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

        fun fromMinutes(_minutes: Int): TaskTime {
            val minutes = if (_minutes > 1440) (_minutes - 1440) else _minutes
            val hour = kotlin.math.floor(minutes / 60f).toInt()
            val minute = minutes % 60
            return TaskTime(hour, minute)
        }
    }
}