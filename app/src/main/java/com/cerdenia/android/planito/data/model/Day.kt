package com.cerdenia.android.planito.data.model

import android.content.Context
import android.content.res.Resources
import com.cerdenia.android.planito.R

enum class Day(
    private val nameResID: Int,
    val isWeekday: Boolean = true
) {
    SUNDAY(R.string.sunday, false, ),
    MONDAY(R.string.monday),
    TUESDAY(R.string.tuesday),
    WEDNESDAY(R.string.wednesday),
    THURSDAY(R.string.thursday),
    FRIDAY(R.string.friday),
    SATURDAY(R.string.saturday,false);

    val orderFromMonday = if (ordinal == 0) 6 else ordinal - 1
    val orderFromSunday = ordinal

    fun getName(context: Context): String {
        return context.getString(nameResID)
    }

    fun getName(resources: Resources): String {
        return resources.getString(nameResID)
    }

    companion object {

        fun list(): List<Day> = values().toList()
    }
}