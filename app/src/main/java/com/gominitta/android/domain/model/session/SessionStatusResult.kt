package com.gominitta.android.domain.model.session

import java.time.LocalDateTime

/** 세션 시작/완료 처리(PATCH) 결과 — 상태 전이에 관여하는 필드만 담는다. */
data class SessionStatusResult(
    val id: Long,
    val worryId: Long,
    val status: SessionStatus,
    val startedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val emotionScoreBefore: Int?,
    val emotionScoreAfter: Int?,
)
