package com.cerdenia.android.planito.extension

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

inline fun FragmentManager.transact(
    callback: (FragmentTransaction) -> Unit
) {
    val transaction = this.beginTransaction()
    callback(transaction)
    transaction.commit()
}