package com.cerdenia.android.planito.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object CalendarPermissions {

    interface Launcher {

        val calendarPermissionLauncher: ActivityResultLauncher<Array<out String>>
    }

    val list = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )

    fun isGranted(context: Context): Boolean {
        var isPermitted = false
        for (permission in list) {
            val result = ContextCompat.checkSelfPermission(context, permission)
            isPermitted = result == PackageManager.PERMISSION_GRANTED
        }

        return isPermitted
    }
}