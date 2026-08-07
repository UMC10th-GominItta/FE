package com.gominitta.android.domain.model.mypage

import java.time.LocalTime

data class FavoriteTime(
    val id: Long,
    val label: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
)