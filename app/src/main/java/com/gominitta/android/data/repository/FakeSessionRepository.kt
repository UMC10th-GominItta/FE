package com.gominitta.android.data.repository

import com.gominitta.android.domain.model.session.SessionDetail
import com.gominitta.android.domain.model.session.SessionRecord
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.model.session.SessionStatusResult
import com.gominitta.android.domain.model.session.SessionSummary
import com.gominitta.android.domain.repository.SessionRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * In-memory fake — 실제 서버가 준비되기 전까지 마음 세션 화면 UI를 눈으로 확인하기 위한 더미 데이터.
 * API 명세서 예시값과 동일하게 맞춰뒀다. 서버 연동 준비되면 [com.gominitta.android.di.AppModule]
 * 의 `@Binds` 대상을 [SessionRepositoryImpl] 로 교체.
 */
class FakeSessionRepository @Inject constructor() : SessionRepository {

    private val summaries = mutableListOf(
        SessionSummary(
            id = 1,
            worryId = 10,
            worryContent = "UMC 프론트가 안 구해지면 어떡하지",
            status = SessionStatus.SCHEDULED,
            scheduledStartAt = LocalDateTime.of(2026, 5, 27, 22, 0),
            scheduledEndAt = LocalDateTime.of(2026, 5, 27, 23, 0),
        ),
        SessionSummary(
            id = 3,
            worryId = 12,
            worryContent = "UMC 프론트가 안 구해지면 어떡하지",
            status = SessionStatus.SCHEDULED,
            scheduledStartAt = LocalDateTime.of(2026, 5, 28, 23, 0),
            scheduledEndAt = LocalDateTime.of(2026, 5, 29, 0, 0),
        ),
        SessionSummary(
            id = 2,
            worryId = 11,
            worryContent = "UMC 디자이너가 안 구해지면 어떡하지",
            status = SessionStatus.INCOMPLETE,
            scheduledStartAt = LocalDateTime.of(2026, 5, 19, 23, 0),
            scheduledEndAt = LocalDateTime.of(2026, 5, 20, 0, 0),
        ),
    )

    private val details = mutableMapOf(
        1L to SessionDetail(
            id = 1,
            worryId = 10,
            worryContent = "UMC 프론트가 안 구해지면 어떡하지",
            themeCategory = "진로",
            status = SessionStatus.SCHEDULED,
            scheduledStartAt = LocalDateTime.of(2026, 5, 27, 22, 0),
            scheduledEndAt = LocalDateTime.of(2026, 5, 27, 23, 0),
            startedAt = null,
            completedAt = null,
            emotionScoreBefore = 8,
            emotionScoreAfter = null,
            records = listOf(
                SessionRecord(
                    id = 5,
                    recordType = "text",
                    contentText = "지금 드는 생각을 그대로 적었어요.",
                    mediaUrl = null,
                    createdAt = LocalDateTime.of(2026, 5, 27, 22, 10),
                ),
            ),
        ),
    )

    override suspend fun getSessions(status: SessionStatus?): List<SessionSummary> =
        summaries.filter { status == null || it.status == status }

    override suspend fun getSessionDetail(sessionId: Long): SessionDetail =
        details[sessionId] ?: error("Fake 데이터에 session id=$sessionId 상세가 없습니다.")

    override suspend fun startSession(sessionId: Long): SessionStatusResult {
        val updated = requireDetail(sessionId).copy(
            status = SessionStatus.IN_PROGRESS,
            startedAt = LocalDateTime.now(),
        )
        details[sessionId] = updated
        return updated.toStatusResult()
    }

    override suspend fun completeSession(sessionId: Long, emotionScoreAfter: Int): SessionStatusResult {
        val updated = requireDetail(sessionId).copy(
            status = SessionStatus.COMPLETED,
            completedAt = LocalDateTime.now(),
            emotionScoreAfter = emotionScoreAfter,
        )
        details[sessionId] = updated
        return updated.toStatusResult()
    }

    private fun requireDetail(sessionId: Long): SessionDetail =
        details[sessionId] ?: error("Fake 데이터에 session id=$sessionId 상세가 없습니다.")

    private fun SessionDetail.toStatusResult() = SessionStatusResult(
        id = id,
        worryId = worryId,
        status = status,
        startedAt = startedAt,
        completedAt = completedAt,
        emotionScoreBefore = emotionScoreBefore,
        emotionScoreAfter = emotionScoreAfter,
    )
}
