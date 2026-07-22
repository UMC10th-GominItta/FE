package com.gominitta.android.presentation.worry

import java.time.LocalDateTime

data class WorryReservationUiState(
    val title: String = "",
    val content: String = "",
    val intensity: Int = 5,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
)
