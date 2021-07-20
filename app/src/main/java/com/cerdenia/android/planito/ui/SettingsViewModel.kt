package com.cerdenia.android.planito.ui

import androidx.lifecycle.*
import com.cerdenia.android.planito.data.AppRepository
import com.cerdenia.android.planito.data.model.UserCalendar

class SettingsViewModel(
    private val repo: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    private val ownerAccountLive = MutableLiveData<String>()
    private val calendarsLive: LiveData<List<UserCalendar>> = Transformations
        .switchMap(ownerAccountLive) { repo.getUserCalendars(it) }

    val calendars get() = calendarsLive.value

    private val _calendarNamesLive = MediatorLiveData<Array<String>>()
    val calendarNamesLive: LiveData<Array<String>> get() = _calendarNamesLive

    init {
        _calendarNamesLive.addSource(calendarsLive) { calendars ->
            _calendarNamesLive.value = calendars
                .map { it.displayName }
                .toTypedArray()
        }
    }

    fun getUserCalendars(ownerAccount: String) {
        ownerAccountLive.value = ownerAccount
    }

    companion object {

        const val DEFAULT_CALENDAR = "default_calendar"
    }
}