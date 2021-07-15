package com.cerdenia.android.planito.data

data class TaskTime(
    var hour: Int = 0,
    var minute: Int = 0,
) {

    fun toMinutes(): Int {
        return (hour * 60) + minute
    }

    fun to24HourFormat(): String {
        val (hour, minute) = listOf(this.hour, this.minute).map {
            if (it.toString().length == 1) "0$it" else it
        }

        return "$hour:$minute"
    }

    companion object {

        fun fromMinutes(minutes: Int): TaskTime {
            val hour = kotlin.math.floor(minutes / 60.0).toInt()
            val minute = minutes % 60
            return TaskTime(hour, minute)
        }
    }
}