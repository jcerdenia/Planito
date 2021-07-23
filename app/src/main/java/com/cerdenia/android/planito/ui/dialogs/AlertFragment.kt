package com.cerdenia.android.planito.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.cerdenia.android.planito.R

// Default dialog fragment when no fragment result is required.
class AlertFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getInt(TITLE_RES_ID)
        val message = arguments?.getInt(MESSAGE_RES_ID)
        val icon = arguments?.getInt(ICON) ?: R.drawable.ic_alert

        return if (title != null && message != null ) {
            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setIcon(icon)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                .create()
        } else {
            super.onCreateDialog(savedInstanceState)
        }
    }

    companion object {

        const val TAG = "AlertFragment"

        private const val TITLE_RES_ID = "title_res_id"
        private const val MESSAGE_RES_ID = "message_res_id"
        private const val ICON = "icon"

        fun newInstance(
            titleResID: Int,
            messageResID: Int,
            icon: Int = R.drawable.ic_alert
        ): AlertFragment {
            return AlertFragment().apply {
                arguments = Bundle().apply {
                    putInt(TITLE_RES_ID, titleResID)
                    putInt(MESSAGE_RES_ID, messageResID)
                    putInt(ICON, icon)
                }
            }
        }
    }
}