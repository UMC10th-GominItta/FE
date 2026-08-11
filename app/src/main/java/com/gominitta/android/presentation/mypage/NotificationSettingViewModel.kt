package com.gominitta.android.presentation.mypage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.usecase.GetNotificationSettingUseCase
import com.gominitta.android.domain.usecase.UpdateNotificationSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationSettingViewModel @Inject constructor(
    private val getNotificationSetting: GetNotificationSettingUseCase,
    private val updateNotificationSetting: UpdateNotificationSettingUseCase,
) : ViewModel() {

    var reservationReminderEnabled by mutableStateOf(false)
        private set

    var sessionStartReminderEnabled by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            when (val result = getNotificationSetting()) {
                is ApiResult.Success -> {
                    reservationReminderEnabled = result.data.worryReminderEnabled
                    sessionStartReminderEnabled = result.data.sessionStartEnabled
                }
                is ApiResult.Error -> errorMessage = result.message
                is ApiResult.NetworkError -> errorMessage = "네트워크 연결을 확인해 주세요."
            }
        }
    }

    fun onReservationReminderChanged(enabled: Boolean) {
        val previous = reservationReminderEnabled
        reservationReminderEnabled = enabled
        viewModelScope.launch {
            when (val result = updateNotificationSetting(worryReminderEnabled = enabled)) {
                is ApiResult.Success -> errorMessage = null
                is ApiResult.Error -> {
                    reservationReminderEnabled = previous
                    errorMessage = result.message
                }
                is ApiResult.NetworkError -> {
                    reservationReminderEnabled = previous
                    errorMessage = "네트워크 연결을 확인해 주세요."
                }
            }
        }
    }

    fun onSessionStartReminderChanged(enabled: Boolean) {
        val previous = sessionStartReminderEnabled
        sessionStartReminderEnabled = enabled
        viewModelScope.launch {
            when (val result = updateNotificationSetting(sessionStartEnabled = enabled)) {
                is ApiResult.Success -> errorMessage = null
                is ApiResult.Error -> {
                    sessionStartReminderEnabled = previous
                    errorMessage = result.message
                }
                is ApiResult.NetworkError -> {
                    sessionStartReminderEnabled = previous
                    errorMessage = "네트워크 연결을 확인해 주세요."
                }
            }
        }
    }
}
