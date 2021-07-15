package com.cerdenia.android.planito.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.data.TaskTime

class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val requestKey = arguments?.getString(REQUEST_KEY)
        val hour = arguments?.getInt(HOUR) ?: 0
        val minute = arguments?.getInt(MINUTE) ?: 0

        val timeListener = requestKey?.let { key ->
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                setFragmentResult(key, Bundle().apply {
                    putInt(HOUR, hour)
                    putInt(MINUTE, minute)
                })
            }
        }

        return TimePickerDialog(context, timeListener, hour, minute, false)
    }

    companion object {

        const val TAG = "TimePickerFragment"
        const val REQUEST_KEY = "request_key"
        const val HOUR = "hour"
        const val MINUTE = "minute"

        fun newInstance(time: TaskTime, requestKey: String): TimePickerFragment {
            return TimePickerFragment().apply {
                arguments = Bundle().apply {
                    putString(REQUEST_KEY, requestKey)
                    putInt(HOUR, time.hour)
                    putInt(MINUTE, time.minute)
                }
            }
        }
    }
}