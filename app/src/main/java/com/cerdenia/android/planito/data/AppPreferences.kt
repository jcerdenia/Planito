package com.cerdenia.android.planito.data

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {

    private lateinit var preferences: SharedPreferences

    private const val NAME = "AppPreferences"
    private const val CALENDAR_EVENT_IDS = "calendar_event_ids"
    private const val CALENDAR_ID = "calendar_id"
    private const val CALENDAR_OWNER = "calendar_owner"

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

    val calendarID: Long
        get() = preferences.getLong(CALENDAR_ID, 1)

    val calendarOwner: String
        get() = preferences.getString(CALENDAR_OWNER, "") ?: ""

    fun setUserCalendarDetails(id: Long, name: String) {
        preferences.edit { editor ->
            editor.putLong(CALENDAR_ID, id)
            editor.putString(CALENDAR_OWNER, name)
        }
    }
}