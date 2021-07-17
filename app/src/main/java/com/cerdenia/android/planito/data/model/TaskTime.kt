package com.cerdenia.android.planito.data.model

data class TaskTime(
    var hour: Int = 0,
    var minute: Int = 0,
) {

    fun toMinutes(): Int = (hour * 60) + minute

    private fun to24HourFormat(): String {
        fun Int.formatted(): String = if (this.toString().length == 1) "0$this" else "$this"
        return "${hour.formatted()}:${minute.formatted()}"
    }

    fun to12HourFormat(): String {
        val timeString = this.to24HourFormat()
        val hour = timeString.substringBefore(":").toInt()
        val minute = timeString.substringAfter(":")

        return when (hour) {
            0 -> "12:$minute AM"
            in 1..11 -> "$hour:$minute AM"
            12 -> "12:$minute PM"
            in 13..23 -> "${hour - 12}:$minute PM"
            else -> throw IllegalStateException("Hour must not be more than 23.")
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