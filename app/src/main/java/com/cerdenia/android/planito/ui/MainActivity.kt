package com.cerdenia.android.planito.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.cerdenia.android.planito.R
import com.cerdenia.android.planito.databinding.ActivityMainBinding
import com.cerdenia.android.planito.ui.settings.SettingsFragment
import com.cerdenia.android.planito.ui.taskdetail.TaskDetailFragment
import com.cerdenia.android.planito.ui.tasklist.TaskListFragment
import java.util.*

class MainActivity : AppCompatActivity(), TaskListFragment.Callbacks {

    private lateinit var binding: ActivityMainBinding
    private var container = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        container = binding.fragmentContainer.id
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(container, TaskListFragment.newInstance())
            }
        }
    }

    override fun onTaskSelected(taskID: UUID, isNew: Boolean) {
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            replace(container, TaskDetailFragment.newInstance(taskID, isNew))
            addToBackStack(null)
        }
    }

    override fun onTaskSettingsClicked() {
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            replace(container, SettingsFragment.newInstance())
            addToBackStack(null)
        }
    }
}