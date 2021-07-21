package com.cerdenia.android.planito.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.cerdenia.android.planito.databinding.ActivityMainBinding
import com.cerdenia.android.planito.ui.settings.SettingsFragment
import com.cerdenia.android.planito.ui.taskdetail.TaskDetailFragment
import com.cerdenia.android.planito.ui.tasklist.TaskListFragment
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
            supportFragmentManager.transact { transaction ->
                transaction.add(fragCon, TaskListFragment.newInstance())
            }
        }
    }

    override fun onTaskSelected(taskID: UUID) {
        supportFragmentManager.transact { transaction ->
            transaction.replace(fragCon, TaskDetailFragment.newInstance(taskID))
            transaction.addToBackStack(null)
        }
    }

    override fun onTaskSettingsClicked() {
        supportFragmentManager.transact { transaction ->
            transaction.replace(fragCon, SettingsFragment.newInstance())
            transaction.addToBackStack(null)
        }
    }

    override fun onTaskSavedOrDeleted() {
        onBackPressed()
    }

    private inline fun FragmentManager.transact(
        function: (FragmentTransaction) -> Unit
    ) {
        val transaction = this.beginTransaction()
        function(transaction)
        transaction.commit()
    }
}