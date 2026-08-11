package com.gominitta.android.domain.model.worry

import java.time.LocalDateTime

data class Worry(
    val id: Long,
    val title: String,
    val content: String,
    val emotionScoreBefore: Int,
    val scheduledStartAt: LocalDateTime,
    val scheduledEndAt: LocalDateTime,
)
