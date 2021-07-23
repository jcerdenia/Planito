package com.cerdenia.android.planito.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.utils.OnTextChangedListener

class NewTaskFragment : DialogFragment() {

    private var positiveButton: Button? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val taskNameField = AppCompatEditText(requireContext()).apply {
            hint = getString(R.string.task_name)
            isSingleLine = true
            inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            addTextChangedListener(OnTextChangedListener { text ->
                positiveButton?.isEnabled = text.isNotBlank()
            })
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_task)
            .setView(FrameLayout(requireContext()).apply {
                setPaddingRelative(60, 16, 60, 0)
                addView(taskNameField)
            })
            .setCancelable(true)
            .setPositiveButton(R.string.continue_text) { dialog, _ ->
                arguments?.getString(REQUEST_KEY)?.let { requestKey ->
                    setFragmentResult(requestKey, Bundle().apply {
                        putString(TASK_NAME, taskNameField.text.toString())
                    })
                }
                dialog?.dismiss()
            }
            .create()
    }

    override fun onStart() {
        super.onStart()
        // After dialog is visible, initialize positive button state.
        positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton?.isEnabled = false // Initial state
    }

    companion object {

        const val TAG = "NewTaskFragment"
        const val TASK_NAME = "task_name"

        private const val REQUEST_KEY = "request_key"

        fun newInstance(requestKey: String): NewTaskFragment {
            return NewTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(REQUEST_KEY, requestKey)
                }
            }
        }
    }
}