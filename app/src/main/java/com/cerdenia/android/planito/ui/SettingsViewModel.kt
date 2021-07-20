package com.cerdenia.android.planito.ui

import androidx.lifecycle.*
import com.cerdenia.android.planito.data.AppPreferences
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.model.UserCalendar

class SettingsViewModel(
    private val repo: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    private val ownerAccountLive = MutableLiveData<String>()
    private val calendarsLive: LiveData<List<UserCalendar>> = Transformations
        .switchMap(ownerAccountLive) { repo.getUserCalendars(it) }

    private val _calendarNamesLive = MediatorLiveData<Array<String>>()
    val calendarNamesLive: LiveData<Array<String>> get() = _calendarNamesLive

    val calendars get() = calendarsLive.value
    val userCalendarID get() = AppPreferences.calendarID
    val userCalendarOwner get() = AppPreferences.calendarOwner

    init {
        _calendarNamesLive.addSource(calendarsLive) { calendars ->
            _calendarNamesLive.value = calendars
                .map { it.displayName }
                .toTypedArray()
        }
    }

    fun fetchUserCalendars(accountName: String = userCalendarOwner) {
        ownerAccountLive.value = accountName
    }

    fun setCalendarSelection(index: Int) {
        calendars?.get(index)?.let { calendar ->
            AppPreferences.setUserCalendarDetails(calendar.id, calendar.ownerAccount)
        }
    }
}