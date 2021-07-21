package com.cerdenia.android.planito.util

import android.text.Editable
import android.text.TextWatcher

class OnTextChangedListener(
    private val callback: (String) -> Unit
) : TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // Do nothing.
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // Do nothing.
    }

    override fun afterTextChanged(p0: Editable?) {
        callback(p0.toString())
    }
}