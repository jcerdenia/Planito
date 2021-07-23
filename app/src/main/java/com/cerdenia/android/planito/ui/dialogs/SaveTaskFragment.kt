package com.cerdenia.android.planito.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.R

class SaveTaskFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val requestKey = arguments?.getString(REQUEST_KEY)
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.save_changes_to_task)
            .setCancelable(true)
            .setPositiveButton(R.string.yes) { dialog, _ ->
               requestKey?.let { key ->
                   setFragmentResult(key, Bundle().apply {
                       putBoolean(IS_POSITIVE, true)
                   })
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                requestKey?.let { key ->
                    setFragmentResult(key, Bundle().apply {
                        putBoolean(IS_POSITIVE, false)
                    })
                }
                dialog.dismiss()
            }
            .create()
    }

    companion object {

        const val TAG = "SaveTaskFragment"
        const val IS_POSITIVE = "is_positive"
        private const val REQUEST_KEY = "request_key"

        fun newInstance(requestKey: String): SaveTaskFragment {
            return SaveTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(REQUEST_KEY, requestKey)
                }
            }
        }
    }
}