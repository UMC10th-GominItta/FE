package com.gominitta.android.data.repository

import com.gominitta.android.domain.model.session.RecordType
import com.gominitta.android.domain.model.session.Session
import com.gominitta.android.domain.model.session.SessionRecord
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.repository.SessionRepository
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

/**
 * In-memory fake — 실제 서버가 준비되기 전까지 마음 세션 화면 UI를 눈으로 확인하기 위한 더미 데이터.
 * API 명세서 예시값과 동일하게 맞춰뒀다. 서버 연동 전으로 되돌리려면 [com.gominitta.android.di.SessionModule]
 * 의 `@Binds` 대상을 이 클래스로 교체.
 */
class FakeSessionRepository @Inject constructor() : SessionRepository {

    private val sessions = mutableMapOf(
        1L to Session(
            id = 1,
            worryId = 10,
            worryContent = "UMC 프론트가 안 구해지면 어떡하지",
            scheduledStartAt = LocalDateTime.of(2026, 5, 27, 22, 0),
            scheduledEndAt = LocalDateTime.of(2026, 5, 27, 23, 0),
            status = SessionStatus.SCHEDULED,
            emotionScoreBefore = 8,
            themeCategory = "진로",
            records = listOf(
                SessionRecord(
                    id = 5,
                    recordType = RecordType.TEXT,
                    contentText = "지금 드는 생각을 그대로 적었어요.",
                    mediaUrl = null,
                    createdAt = LocalDateTime.of(2026, 5, 27, 22, 10),
                ),
            ),
        ),
        3L to Session(
            id = 3,
            worryId = 12,
            worryContent = "UMC 프론트가 안 구해지면 어떡하지",
            scheduledStartAt = LocalDateTime.of(2026, 5, 28, 23, 0),
            scheduledEndAt = LocalDateTime.of(2026, 5, 29, 0, 0),
            status = SessionStatus.SCHEDULED,
            emotionScoreBefore = 8,
            themeCategory = "진로",
        ),
        2L to Session(
            id = 2,
            worryId = 11,
            worryContent = "UMC 디자이너가 안 구해지면 어떡하지",
            scheduledStartAt = LocalDateTime.of(2026, 5, 19, 23, 0),
            scheduledEndAt = LocalDateTime.of(2026, 5, 20, 0, 0),
            status = SessionStatus.INCOMPLETE,
            emotionScoreBefore = 7,
            themeCategory = "진로",
        ),
    )

    private val nextRecordId = AtomicLong(100)

    override suspend fun getSessions(status: SessionStatus?): List<Session> =
        sessions.values.filter { status == null || it.status == status }

    override suspend fun getSessionDetail(sessionId: Long): Session = requireSession(sessionId)

    override suspend fun startSession(sessionId: Long): Session {
        val updated = requireSession(sessionId).copy(
            status = SessionStatus.IN_PROGRESS,
            startedAt = LocalDateTime.now(),
        )
        sessions[sessionId] = updated
        return updated
    }

    override suspend fun completeSession(sessionId: Long, emotionScoreAfter: Int): Session {
        val updated = requireSession(sessionId).copy(
            status = SessionStatus.COMPLETED,
            completedAt = LocalDateTime.now(),
            emotionScoreAfter = emotionScoreAfter,
        )
        sessions[sessionId] = updated
        return updated
    }

    // ── 세션 기록 ───────────────────────────────────────────────────────

    override suspend fun getRecords(sessionId: Long, recordType: RecordType?): List<SessionRecord> =
        requireSession(sessionId).records.filter { recordType == null || it.recordType == recordType }

    override suspend fun createTextRecord(sessionId: Long, contentText: String): SessionRecord =
        addRecord(sessionId, RecordType.TEXT, contentText, mediaUrl = null)

    override suspend fun createVoiceRecord(sessionId: Long, file: File): SessionRecord =
        addRecord(
            sessionId,
            RecordType.VOICE,
            contentText = "(음성 인식 결과 예시) 지금 드는 생각을 자유롭게 말했어요.",
            mediaUrl = file.toURI().toString(),
        )

    override suspend fun createHandwritingRecord(sessionId: Long, file: File): SessionRecord =
        addRecord(
            sessionId,
            RecordType.HANDWRITING,
            contentText = "(손글씨 인식 결과 예시) 노트에 적어둔 내용이에요.",
            mediaUrl = file.toURI().toString(),
        )

    override suspend fun updateRecord(sessionId: Long, recordId: Long, contentText: String): SessionRecord {
        val session = requireSession(sessionId)
        val target = session.records.find { it.id == recordId }
            ?: error("Fake 데이터에 session id=$sessionId record id=$recordId 가 없습니다.")
        val updated = target.copy(contentText = contentText)
        sessions[sessionId] = session.copy(records = session.records.map { if (it.id == recordId) updated else it })
        return updated
    }

    override suspend fun deleteRecord(sessionId: Long, recordId: Long) {
        val session = requireSession(sessionId)
        sessions[sessionId] = session.copy(records = session.records.filterNot { it.id == recordId })
    }

    private fun addRecord(sessionId: Long, recordType: RecordType, contentText: String, mediaUrl: String?): SessionRecord {
        val session = requireSession(sessionId)
        val record = SessionRecord(
            id = nextRecordId.getAndIncrement(),
            recordType = recordType,
            contentText = contentText,
            mediaUrl = mediaUrl,
            createdAt = LocalDateTime.now(),
        )
        sessions[sessionId] = session.copy(records = session.records + record)
        return record
    }

    private fun requireSession(sessionId: Long): Session =
        sessions[sessionId] ?: error("Fake 데이터에 session id=$sessionId 가 없습니다.")
}
