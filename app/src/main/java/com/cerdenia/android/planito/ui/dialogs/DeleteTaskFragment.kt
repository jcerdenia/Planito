package com.cerdenia.android.planito.ui.dialogs

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.R

class DeleteTaskFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val taskName = arguments?.getString(TASK_NAME) ?: getString(R.string.task)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_task, taskName))
            .setCancelable(true)
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                setFragmentResult(CONFIRM_DELETE, Bundle.EMPTY)
                dialog.dismiss()
            }
            .create()

        dialog.show()
        return dialog
    }

    companion object {

        const val TAG = "DeleteTaskFragment"
        const val CONFIRM_DELETE = "confirm_delete"
        private const val TASK_NAME = "task_name"

        fun newInstance(taskName: String): DeleteTaskFragment {
            return DeleteTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(TASK_NAME, taskName)
                }
            }
        }
    }
}