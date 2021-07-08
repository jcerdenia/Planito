package com.cerdenia.android.planito.extension

import android.text.Editable

fun String?.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)