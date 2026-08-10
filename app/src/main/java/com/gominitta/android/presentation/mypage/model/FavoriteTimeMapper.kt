package com.gominitta.android.presentation.mypage.model

import com.gominitta.android.domain.model.mypage.FavoriteTime
import java.time.LocalTime

fun TimeValue.toLocalTime(): LocalTime = LocalTime.of(to24Hour(), minute)

fun LocalTime.toTimeValue(): TimeValue {
    val isPm = hour >= 12
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return TimeValue(hour = hour12, minute = minute, isPm = isPm)
}

fun FavoriteTime.toUiModel(): FavoriteTimeUiModel = FavoriteTimeUiModel(
    id = id,
    title = label,
    startTime = startTime.toTimeValue(),
    endTime = endTime.toTimeValue(),
)

fun FavoriteTimeUiModel.toDomain(): FavoriteTime = FavoriteTime(
    id = id,
    label = title,
    startTime = startTime.toLocalTime(),
    endTime = endTime.toLocalTime(),
)