package com.cerdenia.android.planito.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.R

class ConfirmSyncFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val requestKey = arguments?.getString(REQUEST_KEY)
        val calendarName = arguments?.getString(CALENDAR_NAME)

        return if (requestKey != null && calendarName != null) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.sync_to_calendar, calendarName))
                .setCancelable(true)
                .setPositiveButton(R.string.confirm) { dialog, _ ->
                    setFragmentResult(requestKey, Bundle.EMPTY)
                    dialog.dismiss()
                }
                .create()
        } else {
            super.onCreateDialog(savedInstanceState)
        }
    }

    companion object {

        const val TAG = "ConfirmSyncFragment"
        const val CONFIRM_SYNC = "confirm_sync"

        private const val REQUEST_KEY = "request_key"
        private const val CALENDAR_NAME = "calendar_name"

        fun newInstance(requestKey: String, calendarName: String): ConfirmSyncFragment {
            return ConfirmSyncFragment().apply {
                arguments = Bundle().apply {
                    putString(REQUEST_KEY, requestKey)
                    putString(CALENDAR_NAME, calendarName)
                }
            }
        }
    }
}