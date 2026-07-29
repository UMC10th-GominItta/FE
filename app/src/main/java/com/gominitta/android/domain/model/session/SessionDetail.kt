package com.gominitta.android.domain.model.session

import java.time.LocalDateTime

/** 마음 세션 상세(C102) — 예약된 걱정 + 진행 정보 + 기록. */
data class SessionDetail(
    val id: Long,
    val worryId: Long,
    val worryContent: String,
    val themeCategory: String,
    val status: SessionStatus,
    val scheduledStartAt: LocalDateTime,
    val scheduledEndAt: LocalDateTime,
    val startedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val emotionScoreBefore: Int?,
    val emotionScoreAfter: Int?,
    val records: List<SessionRecord>,
)