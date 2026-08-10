package com.gominitta.android.data.mapper

import com.gominitta.android.data.remote.worry.dto.WorryDetailResponse
import com.gominitta.android.domain.model.worry.Worry

fun WorryDetailResponse.toDomain() = Worry(
    id = id,
    content = content,
    emotionScoreBefore = emotionScoreBefore,
    scheduledStartAt = scheduledStartAt.toLocalDateTime(),
    scheduledEndAt = scheduledEndAt.toLocalDateTime(),
)
