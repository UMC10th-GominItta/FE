package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.session.RecordType
import com.gominitta.android.domain.model.session.Session
import com.gominitta.android.domain.model.session.SessionRecord
import com.gominitta.android.domain.repository.SessionRepository
import java.io.File
import javax.inject.Inject

/** 마음 세션 상세(C102). 기록 목록도 함께 담겨 오므로 별도 조회 불필요. */
class GetSessionDetailUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(sessionId: Long): Session =
        repository.getSessionDetail(sessionId)
}

/** 세션 상세 → "시작하기". */
class StartSessionUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(sessionId: Long): Session =
        repository.startSession(sessionId)
}

/**
 * 세션 평가 → "저장". 서버가 0~10 을 요구하므로 호출 전에 막는다
 * (범위를 벗어나면 서버 왕복 없이 즉시 실패).
 */
class CompleteSessionUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(sessionId: Long, emotionScoreAfter: Int): Session {
        require(emotionScoreAfter in EMOTION_SCORE_RANGE) {
            "세션 후 감정 점수는 ${EMOTION_SCORE_RANGE.first}~${EMOTION_SCORE_RANGE.last} 사이여야 합니다."
        }
        return repository.completeSession(sessionId, emotionScoreAfter)
    }

    private companion object {
        val EMOTION_SCORE_RANGE = 0..10
    }
}

/** 유형별 기록 조회 — [recordType] null 이면 전체. */
class GetSessionRecordsUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(
        sessionId: Long,
        recordType: RecordType? = null,
    ): List<SessionRecord> = repository.getRecords(sessionId, recordType)
}

/**
 * 세션 진행 중 텍스트 탭에 적은 내용을 기록으로 저장한다.
 * 빈 문자열 체크는 호출부(SessionActiveViewModel.commitAndProceed)에서 이미 하고 있어
 * 여기서는 그대로 전달만 한다.
 */
class AddTextRecordUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(sessionId: Long, contentText: String): SessionRecord =
        repository.createTextRecord(sessionId, contentText.trim())
}

/** 녹음 파일 업로드 → 서버 STT 변환. */
class AddVoiceRecordUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(sessionId: Long, file: File): SessionRecord =
        repository.createVoiceRecord(sessionId, file)
}

/** 필기 이미지 업로드 → 서버 OCR 변환. */
class AddHandwritingRecordUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(sessionId: Long, file: File): SessionRecord =
        repository.createHandwritingRecord(sessionId, file)
}

class UpdateRecordUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(
        sessionId: Long,
        recordId: Long,
        contentText: String,
    ): SessionRecord = repository.updateRecord(sessionId, recordId, contentText.trim())
}

class DeleteRecordUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(sessionId: Long, recordId: Long) =
        repository.deleteRecord(sessionId, recordId)
}
