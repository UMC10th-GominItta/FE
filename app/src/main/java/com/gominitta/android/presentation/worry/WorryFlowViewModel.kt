package com.gominitta.android.presentation.worry

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 걱정 예약 플로우(B101~B103) 전체에서 공유되는 ViewModel.
 * 걱정 입력 → 강도 선택 → 시간 예약을 거치며 입력값을 누적한다.
 */
@HiltViewModel
class WorryFlowViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WorryReservationUiState())
    val uiState: StateFlow<WorryReservationUiState> = _uiState.asStateFlow()

    fun updateNote(title: String, content: String) {
        _uiState.value = _uiState.value.copy(title = title, content = content)
    }

    fun updateIntensity(intensity: Int) {
        _uiState.value = _uiState.value.copy(intensity = intensity)
    }

    fun updateSchedule(startTime: LocalDateTime, endTime: LocalDateTime) {
        _uiState.value = _uiState.value.copy(startTime = startTime, endTime = endTime)
    }

    fun save() {
        // TODO: 백엔드 API 스펙 확정 후 예약 저장 연동
    }
}
