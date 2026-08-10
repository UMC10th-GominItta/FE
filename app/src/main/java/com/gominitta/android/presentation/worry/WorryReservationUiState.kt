package com.gominitta.android.presentation.worry

import java.time.LocalDateTime

/** 걱정 예약 플로우(WorryInput → WorryIntensity → WorrySchedule → WorrySaved) 화면 전체의 상태입니다. */
data class WorryReservationUiState(
    val title: String = "",
    val content: String = "",
    val intensity: Int = 5,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val saveState: WorrySaveState = WorrySaveState.Idle,
)
