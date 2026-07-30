package com.gominitta.android.domain.repository

import com.gominitta.android.domain.model.session.SessionDetail
import com.gominitta.android.domain.model.session.SessionRecord
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.model.session.SessionStatusResult
import com.gominitta.android.domain.model.session.SessionSummary
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

/**
 * Data-layer seam for 마음 세션, declared in the DOMAIN layer (dependency inversion).
 * Implemented by [com.gominitta.android.data.repository.LocalSessionRepository].
 */
interface SessionRepository {
    /** [status] null = 전체 조회(예정 + 미완료). 저장소가 바뀌면 화면이 자동으로 갱신되도록 Flow. */
    fun getSessions(status: SessionStatus? = null): Flow<List<SessionSummary>>

    suspend fun getSessionDetail(sessionId: Long): SessionDetail

    /** 걱정 예약 — 새 세션을 만들어 목록에 반영한다. [emotionScoreBefore]는 예약 시점의 불안 수치(0~10). */
    suspend fun createSession(
        worryContent: String,
        worryMemo: String = "",
        scheduledStartAt: LocalDateTime,
        scheduledEndAt: LocalDateTime,
        emotionScoreBefore: Int? = null,
    ): SessionSummary

    /** 예약된 걱정 수정(C105) — 메모와 시간만 바꾼다(제목은 읽기 전용). */
    suspend fun updateSession(
        sessionId: Long,
        worryMemo: String,
        scheduledStartAt: LocalDateTime,
        scheduledEndAt: LocalDateTime,
    ): SessionSummary

    suspend fun deleteSession(sessionId: Long)

    /** 마음 세션(C103)에서 기록한 내용을 저장한다. */
    suspend fun addRecord(
        sessionId: Long,
        recordType: String,
        contentText: String?,
        mediaUrl: String?,
    ): SessionRecord

    suspend fun startSession(sessionId: Long): SessionStatusResult

    /** [emotionScoreAfter] 세션 후 불안 수치(0~10). */
    suspend fun completeSession(sessionId: Long, emotionScoreAfter: Int): SessionStatusResult
}
