package com.cerdenia.android.planito.ui.taskdetail

import android.content.Context
import android.widget.CheckBox
import com.cerdenia.android.planito.data.Day
import com.cerdenia.android.planito.databinding.FragmentTaskDetailBinding

class DayCheckBoxesUtil(
    context: Context,
    binding: FragmentTaskDetailBinding
) {

    private val checkBoxes = listOf(
        binding.dayCheckBox0,
        binding.dayCheckBox1,
        binding.dayCheckBox2,
        binding.dayCheckBox3,
        binding.dayCheckBox4,
        binding.dayCheckBox5,
        binding.dayCheckBox6
    )

    private val checkBoxMap = mutableMapOf<Day, CheckBox>()

    init {
        Day.list()
            .sortedBy { it.orderFromMonday }
            .onEachIndexed { i, day -> checkBoxMap[day] = checkBoxes[i] }
            .forEach { day -> checkBoxMap[day]?.text = day.getName(context) }
    }
    
    fun isAnyChecked(): Boolean{
        return checkBoxMap.values.any { it.isChecked }
    }

    fun setIsAnyCheckedListener(callback: (Boolean) -> Unit) {
        checkBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                callback(isAnyChecked())
            }
        }
    }

    fun setSelections(days: Set<Day>) {
        for (day in days) {
            checkBoxMap[day]?.isChecked = true
        }
    }
    
    fun getSelectedDays(): Set<Day> {
        return checkBoxMap
            .filterValues { it.isChecked }
            .keys
    }

    companion object {

        private const val TAG = "DayCheckBoxesUtil"
        const val DEFAULT = 0
        const val MONDAY = 1
    }
}