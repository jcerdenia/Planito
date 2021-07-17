package com.cerdenia.android.planito.data

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {

    private lateinit var preferences: SharedPreferences

    private const val NAME = "AppPreferences"
    private const val CALENDAR_EVENT_IDS = "calendar_event_ids"

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
}