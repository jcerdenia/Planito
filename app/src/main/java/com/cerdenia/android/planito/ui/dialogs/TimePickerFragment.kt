package com.cerdenia.android.planito.ui.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.data.models.TaskTime

class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hour = arguments?.getInt(HOUR) ?: 0
        val minute = arguments?.getInt(MINUTE) ?: 0

        val timeListener = arguments?.getString(REQUEST_KEY)?.let { requestKey ->
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                setFragmentResult(requestKey, Bundle().apply {
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
        private const val HOUR = "hour"
        private const val MINUTE = "minute"

        fun newInstance(requestKey: String, time: TaskTime): TimePickerFragment {
            return TimePickerFragment().apply {
                arguments = Bundle().apply {
                    putString(REQUEST_KEY, requestKey)
                    putInt(HOUR, time.hour)
                    putInt(MINUTE, time.minute)
                }
            }
        }

        fun unbundleFragmentResult(bundle: Bundle): TaskTime {
            val hour = bundle.getInt(HOUR)
            val minute = bundle.getInt(MINUTE)
            return TaskTime(hour, minute)
        }
    }
}