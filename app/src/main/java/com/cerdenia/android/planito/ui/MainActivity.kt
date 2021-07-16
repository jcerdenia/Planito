package com.cerdenia.android.planito.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cerdenia.android.planito.databinding.ActivityMainBinding
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity(),
    TaskListFragment.Callbacks,
    TaskDetailFragment.Callbacks {

    private lateinit var binding: ActivityMainBinding
    private var fragCon = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        fragCon = binding.fragmentContainer.id
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(fragCon, TaskListFragment.newInstance())
                .commit()

            supportActionBar?.title = DateFormat
                .getDateInstance(DateFormat.LONG)
                .format(Date())
        }
    }

    override fun onTaskSelected(taskID: UUID) {
        supportFragmentManager
            .beginTransaction()
            .replace(fragCon, TaskDetailFragment.newInstance(taskID))
            .addToBackStack(null)
            .commit()
    }

    override fun onTaskSavedOrDeleted() {
        onBackPressed()
    }
}