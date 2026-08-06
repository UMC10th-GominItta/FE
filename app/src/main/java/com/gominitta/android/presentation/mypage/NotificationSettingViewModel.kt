package com.gominitta.android.presentation.mypage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel @Inject constructor() : ViewModel() {

    var reservationReminderEnabled by mutableStateOf(false)
        private set

    var sessionStartReminderEnabled by mutableStateOf(true)
        private set

    fun onReservationReminderChanged(enabled: Boolean) {
        reservationReminderEnabled = enabled
    }

    fun onSessionStartReminderChanged(enabled: Boolean) {
        sessionStartReminderEnabled = enabled
    }
}