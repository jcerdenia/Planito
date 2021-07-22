package com.cerdenia.android.planito.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.R

class ConfirmSyncFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val calendarName = arguments?.getString(CALENDAR_NAME)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.sync_to_calendar, calendarName))
            .setCancelable(true)
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                setFragmentResult(CONFIRM_SYNC, Bundle.EMPTY)
                dialog.dismiss()
            }
            .create()

        dialog.show()
        return dialog
    }

    companion object {

        const val TAG = "ConfirmSyncFragment"
        const val CONFIRM_SYNC = "confirm_sync"

        private const val CALENDAR_NAME = "calendar_name"

        fun newInstance(calendarName: String): ConfirmSyncFragment {
            return ConfirmSyncFragment().apply {
                arguments = Bundle().apply {
                    putString(CALENDAR_NAME, calendarName)
                }
            }
        }
    }
}