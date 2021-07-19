package com.cerdenia.android.planito.data

enum class Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    companion object {

        val list: List<Day> get() = values().toList()
    }
}