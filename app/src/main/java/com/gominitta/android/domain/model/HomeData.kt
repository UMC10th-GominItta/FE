package com.gominitta.android.domain.model

import java.time.LocalDateTime

data class HomeData(
    val nickname: String,
    val dailyMessage: String,
    val nextSession: NextSession?,
)

data class NextSession(
    val sessionId: Long,
    val title: String,
    val status: String,
    val startedAt: LocalDateTime,
)
