package com.cerdenia.android.planito.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.cerdenia.android.planito.R

class AlertFragment : DialogFragment() {

    private lateinit var requestKey: String
    private var title: String? = null
    private var message: String? = null
    private var positiveButtonText: String? = null
    private var negativeButtonText: String? = null
    private var shouldBeCancelable = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val typeCode = arguments?.getInt(TYPE_CODE) ?: 0
        val variableText = arguments?.getString(VARIABLE_TEXT)
        setupVariables(typeCode, variableText)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(shouldBeCancelable)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                setFragmentResult(requestKey, Bundle().apply {
                    putBoolean(IS_POSITIVE, true)
                })
                dialog.dismiss()
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
        return dialog
    }

    private fun setupVariables(typeCode: Int, variableText: String?) {
        if (typeCode == TYPE_SAVE_CHANGES) {
            title = getString(R.string.save_changes_to, variableText)
            positiveButtonText = getString(R.string.yes)
            negativeButtonText = getString(R.string.no)
            shouldBeCancelable = true
            requestKey = SAVE_CHANGES
        }
    }

    companion object {

        const val TAG = "AlertFragment"
        const val TYPE_SAVE_CHANGES = 1

        const val SAVE_CHANGES = "save_changes"
        const val IS_POSITIVE = "is_positive"

        private const val TYPE_CODE = "style_code"
        private const val VARIABLE_TEXT = "variable_text"

        fun newInstance(typeCode: Int, variableText: String? = null): DialogFragment {
            return DialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(TYPE_CODE, typeCode)
                    putString(VARIABLE_TEXT, variableText)
                }
            }
        }
    }
}