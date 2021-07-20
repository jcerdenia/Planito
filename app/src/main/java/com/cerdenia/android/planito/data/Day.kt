package com.cerdenia.android.planito.data

import android.content.Context

enum class Day {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    companion object {

        val list: List<Day> get() = values().toList()
    }
}