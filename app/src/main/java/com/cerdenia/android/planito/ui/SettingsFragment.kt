package com.cerdenia.android.planito.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cerdenia.android.planito.data.AppPreferences
import com.cerdenia.android.planito.databinding.FragmentSettingsBinding
import com.cerdenia.android.planito.extension.toEditable
import com.cerdenia.android.planito.util.AfterTextChangedListener

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getUserCalendars(AppPreferences.userCalendarName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.calendarNamesLive.observe(viewLifecycleOwner, { names ->
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                .run { binding.calendarSpinner.adapter = this }

            viewModel.calendars
                ?.map { it.id }
                ?.indexOfFirst { it == AppPreferences.userCalendarID }
                ?.run { binding.calendarSpinner.setSelection(this) }
        })

        binding.calendarOwnerField.apply {
            text = AppPreferences.userCalendarName.toEditable()
            addTextChangedListener(AfterTextChangedListener { id ->
                viewModel.getUserCalendars(id.toString())
            })
        }

        binding.restoreDefaultButton.setOnClickListener {
            binding.calendarOwnerField.text.clear()
        }

        binding.saveButton.setOnClickListener {
            val i = binding.calendarSpinner.selectedItemPosition
            viewModel.calendars?.get(i)?.let { calendar ->
                AppPreferences.setUserCalendarDetails(calendar.id, calendar.ownerAccount)
            }

            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TAG = "SettingsFragment"

        fun newInstance(): SettingsFragment = SettingsFragment()
    }
}