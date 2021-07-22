package com.cerdenia.android.planito.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.utils.OnTextChangedListener

class NewTaskFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        var positiveButton: Button? = null
        val taskNameField = EditText(context).apply {
            hint = getString(R.string.task_name)
            isSingleLine = true
            inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            addTextChangedListener(OnTextChangedListener { text ->
                positiveButton?.isEnabled = text.isNotBlank()
            })
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_task)
            .setView(FrameLayout(requireContext()).apply {
                setPaddingRelative(60, 16, 60, 0)
                addView(taskNameField)
            })
            .setCancelable(true)
            .setPositiveButton(R.string.continue_text) { dialog, _ ->
                setFragmentResult(ADD_TASK, Bundle().apply {
                    putString(TASK_NAME, taskNameField.text.toString())
                })
                dialog?.dismiss()
            }
            .create()

        dialog.show()
        // After showing dialog, initialize positive button state.
        positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.isEnabled = false // Initial state
        return dialog
    }

    companion object {

        const val TAG = "NewTaskFragment"
        const val ADD_TASK = "add_task"
        const val TASK_NAME = "task_name"

        fun newInstance() = NewTaskFragment()
    }
}