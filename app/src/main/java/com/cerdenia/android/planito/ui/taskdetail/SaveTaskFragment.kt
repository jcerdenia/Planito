package com.cerdenia.android.planito.ui.taskdetail

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.R

class SaveTaskFragment : DialogFragment() {

    private lateinit var title: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val requestKey = arguments?.getString(REQUEST_KEY) ?: ""
        val taskName = arguments?.getString(TASK_NAME) ?: ""
        initVariables(requestKey, taskName)

        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setCancelable(true)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                setFragmentResult(requestKey, Bundle().apply {
                    putBoolean(SHOULD_SAVE, true)
                })
                dialog.dismiss()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                setFragmentResult(requestKey, Bundle().apply {
                    putBoolean(SHOULD_SAVE, false)
                })
                dialog.dismiss()
            }
            .create()

        dialog.show()
        return dialog
    }

    private fun initVariables(requestKey: String, taskName: String) {
        when (requestKey) {
            SAVE_CHANGES -> title = getString(R.string.save_changes_to, taskName)
            SAVE_OR_DELETE -> title = getString(R.string.save_task_name, taskName)
        }
    }

    companion object {

        const val TAG = "SaveTaskFragment"
        const val SAVE_OR_DELETE = "save_or_delete"
        const val SAVE_CHANGES = "save_changes"
        const val SHOULD_SAVE = "should_save"

        private const val REQUEST_KEY = "request_key"
        private const val TASK_NAME = "task_name"

        fun newInstance(requestKey: String, taskName: String): SaveTaskFragment {
            return SaveTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(REQUEST_KEY, requestKey)
                    putString(TASK_NAME, taskName)
                }
            }
        }
    }
}