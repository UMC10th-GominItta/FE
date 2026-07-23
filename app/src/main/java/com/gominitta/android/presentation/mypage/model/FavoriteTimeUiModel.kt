package com.gominitta.android.presentation.mypage.model

import java.util.Locale

data class TimeValue(
    val hour: Int = 9,
    val minute: Int = 0,
    val isPm: Boolean = true,
) {
    fun formatted(): String {
        val period = if (isPm) "PM" else "AM"

        return String.format(
            Locale.US,
            "%02d:%02d %s",
            hour,
            minute,
            period,
        )
    }
    fun to24Hour(): Int = when {
        hour == 12 && !isPm -> 0
        hour == 12 && isPm -> 12
        isPm -> hour + 12
        else -> hour
    }
    fun toMinutesOfDay(): Int = to24Hour() * 60 + minute
}

data class FavoriteTimeUiModel(
    val id: Long,
    val title: String,
    val startTime: TimeValue,
    val endTime: TimeValue,
)