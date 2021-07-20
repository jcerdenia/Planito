package com.cerdenia.android.planito.data.model

import android.content.Context
import com.cerdenia.android.planito.R

data class UserCalendar(
    val id: Long,
    val displayName: String,
    val accountName: String,
    val ownerAccount: String,
) {

    companion object {

        fun default(context: Context): UserCalendar {
            return UserCalendar(1, context.getString(R.string.default_name), "", "")
        }
    }
}