package com.cerdenia.android.planito.ui.dialogs

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.R

class DeleteTaskFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_this_task)
            .setCancelable(true)
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                arguments?.getString(REQUEST_KEY)?.let { setFragmentResult(it, Bundle.EMPTY) }
                dialog.dismiss()
            }
            .create()
    }

    companion object {

        const val TAG = "DeleteTaskFragment"

        private const val REQUEST_KEY = "request_key"

        fun newInstance(requestKey: String): DeleteTaskFragment {
            return DeleteTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(REQUEST_KEY, requestKey)
                }
            }
        }
    }
}