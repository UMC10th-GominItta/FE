package com.gominitta.android.domain.repository

import com.gominitta.android.domain.model.session.RecordType
import com.gominitta.android.domain.model.session.Session
import com.gominitta.android.domain.model.session.SessionRecord
import com.gominitta.android.domain.model.session.SessionStatus
import java.io.File

/**
 * Data-layer seam for 마음 세션, declared in the DOMAIN layer (dependency inversion).
 * Implemented by [com.gominitta.android.data.repository.SessionRepositoryImpl].
 *
 * 실패는 예외로 전달된다 — 구현체가 [com.gominitta.android.data.remote.ApiResult] 를
 * 도메인 예외로 변환하므로, 이 인터페이스는 data 레이어 타입을 알지 못한다.
 */
interface SessionRepository {

    // ── 세션 ────────────────────────────────────────────────────────────

    /** [status] null = 전체 조회(예정 + 미완료). */
    suspend fun getSessions(status: SessionStatus? = null): List<Session>

    /** 상세 응답에는 기록 목록(records)이 함께 내려온다 — 기록 조회를 따로 부를 필요 없음. */
    suspend fun getSessionDetail(sessionId: Long): Session

    suspend fun startSession(sessionId: Long): Session

    /** [emotionScoreAfter] 세션 후 불안 수치(0~10). */
    suspend fun completeSession(sessionId: Long, emotionScoreAfter: Int): Session

    // ── 세션 기록 ───────────────────────────────────────────────────────

    /** [recordType] null = 전체 유형. */
    suspend fun getRecords(sessionId: Long, recordType: RecordType? = null): List<SessionRecord>

    suspend fun createTextRecord(sessionId: Long, contentText: String): SessionRecord

    /** 녹음 파일 업로드 → 서버가 STT 변환 후 기록으로 저장. */
    suspend fun createVoiceRecord(sessionId: Long, file: File): SessionRecord

    /** 필기 이미지 업로드 → 서버가 OCR 변환 후 기록으로 저장. */
    suspend fun createHandwritingRecord(sessionId: Long, file: File): SessionRecord

    /** 텍스트 내용만 수정 가능(음성·필기 원본은 교체 불가). */
    suspend fun updateRecord(sessionId: Long, recordId: Long, contentText: String): SessionRecord

    suspend fun deleteRecord(sessionId: Long, recordId: Long)
}
