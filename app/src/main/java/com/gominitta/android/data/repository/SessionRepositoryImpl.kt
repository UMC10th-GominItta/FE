package com.gominitta.android.data.repository

import com.gominitta.android.data.mapper.toDomain
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.SessionApi
import com.gominitta.android.data.remote.dto.RecordUpdateRequestDto
import com.gominitta.android.data.remote.dto.SessionStatusChangeRequestDto
import com.gominitta.android.data.remote.dto.TextRecordCreateRequestDto
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.data.remote.safeApiCallUnit
import com.gominitta.android.domain.model.session.RecordType
import com.gominitta.android.domain.model.session.Session
import com.gominitta.android.domain.model.session.SessionRecord
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.repository.SessionRepository
import java.io.File
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
class SessionRepositoryImpl @Inject constructor(
    private val api: SessionApi,
) : SessionRepository {

    // ── 세션 ────────────────────────────────────────────────────────────

    override suspend fun getSessions(status: SessionStatus?): List<Session> =
        safeApiCall { api.getSessions(status?.raw) }
            .getOrThrow()
            .map { it.toDomain() }

    override suspend fun getSessionDetail(sessionId: Long): Session =
        safeApiCall { api.getSessionDetail(sessionId) }
            .getOrThrow()
            .toDomain()

    override suspend fun startSession(sessionId: Long): Session {
        changeStatus(sessionId, SessionStatus.IN_PROGRESS, emotionScoreAfter = null)
        return getSessionDetail(sessionId)
    }

    override suspend fun completeSession(sessionId: Long, emotionScoreAfter: Int): Session {
        changeStatus(sessionId, SessionStatus.COMPLETED, emotionScoreAfter)
        return getSessionDetail(sessionId)
    }

    private suspend fun changeStatus(sessionId: Long, status: SessionStatus, emotionScoreAfter: Int?) {
        safeApiCall {
            api.changeStatus(
                sessionId = sessionId,
                body = SessionStatusChangeRequestDto(
                    status = status.raw,
                    emotionScoreAfter = emotionScoreAfter,
                ),
            )
        }.getOrThrow()
    }

    // ── 세션 기록 ───────────────────────────────────────────────────────

    override suspend fun getRecords(
        sessionId: Long,
        recordType: RecordType?,
    ): List<SessionRecord> =
        safeApiCall { api.getRecords(sessionId, recordType?.raw) }
            .getOrThrow()
            .map { it.toDomain() }

    override suspend fun createTextRecord(sessionId: Long, contentText: String): SessionRecord =
        safeApiCall { api.createTextRecord(sessionId, TextRecordCreateRequestDto(contentText)) }
            .getOrThrow()
            .toDomain()

    override suspend fun createVoiceRecord(sessionId: Long, file: File): SessionRecord =
        safeApiCall { api.createVoiceRecord(sessionId, file.toPart(AUDIO_MIME)) }
            .getOrThrow()
            .toDomain()

    override suspend fun createHandwritingRecord(sessionId: Long, file: File): SessionRecord =
        safeApiCall { api.createHandwritingRecord(sessionId, file.toPart(IMAGE_MIME)) }
            .getOrThrow()
            .toDomain()

    override suspend fun updateRecord(
        sessionId: Long,
        recordId: Long,
        contentText: String,
    ): SessionRecord =
        safeApiCall { api.updateRecord(sessionId, recordId, RecordUpdateRequestDto(contentText)) }
            .getOrThrow()
            .toDomain()

    override suspend fun deleteRecord(sessionId: Long, recordId: Long) {
        safeApiCallUnit { api.deleteRecord(sessionId, recordId) }.getOrThrow()
    }

    // ── 헬퍼 ────────────────────────────────────────────────────────────

    private fun File.toPart(mimeType: String): MultipartBody.Part =
        MultipartBody.Part.createFormData(
            name = "file",
            filename = name,
            body = asRequestBody(mimeType.toMediaTypeOrNull()),
        )

    private fun <T> ApiResult<T>.getOrThrow(): T = when (this) {
        is ApiResult.Success -> data
        is ApiResult.Error -> throw IllegalStateException(message)
        is ApiResult.NetworkError -> throw IllegalStateException("네트워크 연결을 확인해 주세요.", cause)
    }

    private companion object {
        const val AUDIO_MIME = "audio/*"
        const val IMAGE_MIME = "image/*"
    }
}
