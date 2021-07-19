package com.cerdenia.android.planito.data.model

import com.cerdenia.android.planito.data.Day

data class TaskSansTimes(
    var name: String,
    var description: String,
    var days: Set<Day>
)