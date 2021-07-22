package com.cerdenia.android.planito.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.models.Day
import com.cerdenia.android.planito.data.models.Task
import com.cerdenia.android.planito.data.models.UserCalendar
import java.util.*

class CalendarEditor(private val context: Context) {

    fun getCalendars(ownerAccount: String): LiveData<List<UserCalendar>> {
        val calendarsLive = MutableLiveData<List<UserCalendar>>()
        val calendars = mutableListOf<UserCalendar>()

        // Run query.
        val uri = CalendarContract.Calendars.CONTENT_URI
        val selection = "(${CalendarContract.Calendars.ACCOUNT_NAME} = ?)"
        val selectionArgs = arrayOf(ownerAccount)
        val cursor: Cursor? = context.contentResolver
            .query(uri, EVENT_PROJECTION, selection, selectionArgs, null)

        // Use the cursor to step through the returned records.
        while (cursor?.moveToNext() == true) {
            // Get the field values.
            val calID = cursor.getLong(PROJECTION_ID_INDEX)
            val displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX)
            val accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX)
            val calendar = UserCalendar(calID, displayName, accountName, ownerAccount)
            calendars.add(calendar)
        }

        cursor?.close()
        if (calendars.isEmpty()) calendars.add(UserCalendar.default(context))
        calendarsLive.value = calendars
        return calendarsLive
    }

    fun addEvents(calendarID: Long, tasks: List<Task>): List<String> {
        val eventIDs = mutableListOf<String>()
        val timeZone = TimeZone.getDefault().id

        for (task in tasks) {
            if (task.days.isEmpty()) continue
            val startMillis: Long = task.startTime.toMillis()
            var endMillis: Long = task.endTime.toMillis()
            if (endMillis < startMillis) endMillis += 86400000 // Add one extra day.

            val description = if (task.description.isEmpty()) {
                context.getString(R.string.default_description)
            } else {
                task.description
            }

            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, task.name)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.CALENDAR_ID, calendarID)
                put(CalendarContract.Events.EVENT_TIMEZONE, timeZone)
                put(CalendarContract.Events.RRULE, createRecurrenceRule(task.days))
            }

            val uri: Uri? = context.contentResolver
                .insert(CalendarContract.Events.CONTENT_URI, values)
            // Get the event ID that is the last element in the Uri.
            uri?.lastPathSegment?.let { eventIDs.add(it) }
        }

        Log.d(TAG, "Saved events: $eventIDs")
        return eventIDs
    }

    private fun createRecurrenceRule(_days: Set<Day>): String {
        val rrule = StringBuilder("FREQ=WEEKLY;").append("BYDAY=")
        val days = _days.toList().sortedBy { it.ordinal }

        days.forEachIndexed { i, day ->
            rrule.append(day.name.substring(0, 2)) // Get only first two letters
            if (i < days.lastIndex) rrule.append(",") else rrule.append(";")
        }

        rrule.append("INTERVAL=1")
        return rrule.toString()
    }

    fun deleteEvents(eventIDs: List<String>) {
        for (eventID in eventIDs) {
            val deleteUri: Uri = ContentUris
                .withAppendedId(CalendarContract.Events.CONTENT_URI, eventID.toLong())
            val rows: Int = context.contentResolver
                .delete(deleteUri, null, null)
            Log.d(TAG, "Rows deleted: $rows")
        }
    }

    companion object {

        private const val TAG = "CalendarEditor"

        // Projection array. Creating indices for this array instead of doing
        // dynamic lookups improves performance.
        private val EVENT_PROJECTION = arrayOf(
            CalendarContract.Calendars._ID,                     // 0
            CalendarContract.Calendars.ACCOUNT_NAME,            // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
        )

        // The indices for the projection array above.
        private const val PROJECTION_ID_INDEX = 0
        private const val PROJECTION_ACCOUNT_NAME_INDEX = 1
        private const val PROJECTION_DISPLAY_NAME_INDEX = 2
    }
}