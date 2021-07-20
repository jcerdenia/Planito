package com.cerdenia.android.planito.extension

import android.widget.CheckBox
import com.cerdenia.android.planito.databinding.FragmentTaskDetailBinding

fun FragmentTaskDetailBinding.getDayCheckBoxes(): List<CheckBox> = listOf(
    this.sundayCheckBox,
    this.mondayCheckBox,
    this.tuesdayCheckBox,
    this.wednesdayCheckBox,
    this.thursdayCheckBox,
    this.fridayCheckBox,
    this.saturdayCheckBox
)