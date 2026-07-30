package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.session.SessionSummary
import com.gominitta.android.domain.repository.SessionRepository
import java.time.LocalDateTime
import javax.inject.Inject

/** 걱정 예약(B101~B103) 완료 시점에 호출 — 새 마음 세션을 만들어 목록에 반영한다. */
class CreateSessionUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(
        worryContent: String,
        worryMemo: String = "",
        scheduledStartAt: LocalDateTime,
        scheduledEndAt: LocalDateTime,
        emotionScoreBefore: Int? = null,
    ): SessionSummary = repository.createSession(worryContent, worryMemo, scheduledStartAt, scheduledEndAt, emotionScoreBefore)
}
