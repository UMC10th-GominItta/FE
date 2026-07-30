package com.gominitta.android.domain.model.session

import java.time.LocalDateTime

/** 마음 세션 상세(C102) — 예약된 걱정 + 진행 정보 + 기록. */
data class SessionDetail(
    val id: Long,
    val worryId: Long,
    val worryContent: String,
    /** 걱정 예약 시 함께 적은 상세 메모(WorryInputScreen의 본문 입력). */
    val worryMemo: String,
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