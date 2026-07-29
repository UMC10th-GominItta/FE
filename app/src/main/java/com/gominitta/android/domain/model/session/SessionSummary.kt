package com.gominitta.android.domain.model.session

import java.time.LocalDateTime

/** 마음 세션 목록(C101) 항목. */
data class SessionSummary(
    val id: Long,
    val worryId: Long,
    val worryContent: String,
    val status: SessionStatus,
    val scheduledStartAt: LocalDateTime,
    val scheduledEndAt: LocalDateTime,
)