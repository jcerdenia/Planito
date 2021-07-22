package com.cerdenia.android.planito.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cerdenia.android.planito.databinding.FragmentSettingsBinding
import com.cerdenia.android.planito.extensions.toEditable
import com.cerdenia.android.planito.utils.OnTextChangedListener

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchUserCalendars()
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
                ?.indexOfFirst { it == viewModel.userCalendarID }
                ?.run { binding.calendarSpinner.setSelection(this) }
        })

        binding.calendarOwnerField.apply {
            text = viewModel.userCalendarOwner.toEditable()
            addTextChangedListener(OnTextChangedListener { calendarOwner ->
                viewModel.fetchUserCalendars(calendarOwner)
            })
        }

        binding.restoreDefaultButton.setOnClickListener {
            binding.calendarOwnerField.text.clear()
        }

        binding.saveButton.setOnClickListener {
            val i = binding.calendarSpinner.selectedItemPosition
            viewModel.setCalendarSelection(i)
            activity?.onBackPressed()
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