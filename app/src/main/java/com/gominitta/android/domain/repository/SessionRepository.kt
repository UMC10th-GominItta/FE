package com.gominitta.android.domain.repository

import com.gominitta.android.domain.model.session.SessionDetail
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.model.session.SessionStatusResult
import com.gominitta.android.domain.model.session.SessionSummary

/**
 * Data-layer seam for 마음 세션, declared in the DOMAIN layer (dependency inversion).
 * Implemented by [com.gominitta.android.data.repository.SessionRepositoryImpl].
 */
interface SessionRepository {
    /** [status] null = 전체 조회(예정 + 미완료). */
    suspend fun getSessions(status: SessionStatus? = null): List<SessionSummary>

    suspend fun getSessionDetail(sessionId: Long): SessionDetail

    suspend fun startSession(sessionId: Long): SessionStatusResult

    /** [emotionScoreAfter] 세션 후 불안 수치(0~10). */
    suspend fun completeSession(sessionId: Long, emotionScoreAfter: Int): SessionStatusResult
}
