package com.cerdenia.android.planito.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.data.model.Task
import java.util.*

class CalendarEditor(private val context: Context) {

    fun addEvents(tasks: List<Task>): List<String> {
        val eventIDs = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val timeZone = TimeZone.getDefault().id

        for (task in tasks) {
            val startMillis: Long = calendar.run {
                set(Calendar.HOUR_OF_DAY, task.startTime.hour)
                set(Calendar.MINUTE, task.startTime.minute)
                timeInMillis
            }

            var endMillis: Long = calendar.run {
                set(Calendar.HOUR_OF_DAY, task.endTime.hour)
                set(Calendar.MINUTE, task.endTime.minute)
                timeInMillis
            }

            if (endMillis < startMillis) endMillis += 86400000

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
                put(CalendarContract.Events.CALENDAR_ID, 1) // Primary calendar.
                put(CalendarContract.Events.EVENT_TIMEZONE, timeZone)
                put(CalendarContract.Events.RRULE, "FREQ=DAILY;INTERVAL=1;COUNT=7")
            }

            val uri: Uri? = context.contentResolver
                .insert(CalendarContract.Events.CONTENT_URI, values)
            // Get the event ID that is the last element in the Uri.
            uri?.lastPathSegment?.let { eventIDs.add(it) }
        }

        Log.d(TAG, "Saved events: $eventIDs")
        return eventIDs
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
    }
}