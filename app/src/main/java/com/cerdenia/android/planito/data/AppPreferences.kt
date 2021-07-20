package com.cerdenia.android.planito.data

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {

    private lateinit var preferences: SharedPreferences

    private const val NAME = "AppPreferences"
    private const val CALENDAR_EVENT_IDS = "calendar_event_ids"
    private const val USER_CALENDAR_ID = "calendar_id"
    private const val USER_CALENDAR_NAME = "user_calendar_name"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(callback: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        callback(editor)
        editor.apply()
    }

    var calendarEventIDs: List<String>
        get() = preferences.getStringSet(CALENDAR_EVENT_IDS, setOf())?.toList() ?: listOf()
        set(value) = preferences.edit { editor ->
            editor.putStringSet(CALENDAR_EVENT_IDS, value.toSet())
        }

    val userCalendarID: Long
        get() = preferences.getLong(USER_CALENDAR_ID, 1)

    val userCalendarName: String
        get() = preferences.getString(USER_CALENDAR_NAME, "") ?: ""

    fun setUserCalendarDetails(id: Long, name: String) {
        preferences.edit { editor ->
            editor.putLong(USER_CALENDAR_ID, id)
            editor.putString(USER_CALENDAR_NAME, name)
        }
    }
}