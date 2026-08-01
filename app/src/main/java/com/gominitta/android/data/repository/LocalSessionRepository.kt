package com.gominitta.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gominitta.android.domain.model.session.SessionDetail
import com.gominitta.android.domain.model.session.SessionRecord
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.model.session.SessionStatusResult
import com.gominitta.android.domain.model.session.SessionSummary
import com.gominitta.android.domain.repository.SessionRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val SummariesKey = stringPreferencesKey("session_summaries")
private val DetailsKey = stringPreferencesKey("session_details")

@Serializable
private data class SessionSummaryDto(
    val id: Long,
    val worryId: Long,
    val worryContent: String,
    val status: String,
    val scheduledStartAt: String,
    val scheduledEndAt: String,
)

@Serializable
private data class SessionRecordDto(
    val id: Long,
    val recordType: String,
    val contentText: String?,
    val mediaUrl: String?,
    val createdAt: String,
)

@Serializable
private data class SessionDetailDto(
    val id: Long,
    val worryId: Long,
    val worryContent: String,
    val worryMemo: String = "",
    val themeCategory: String,
    val status: String,
    val scheduledStartAt: String,
    val scheduledEndAt: String,
    val startedAt: String?,
    val completedAt: String?,
    val emotionScoreBefore: Int?,
    val emotionScoreAfter: Int?,
    val records: List<SessionRecordDto>,
)

private fun LocalDateTime.toIso(): String = format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
private fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

private fun SessionSummary.toDto() = SessionSummaryDto(
    id = id,
    worryId = worryId,
    worryContent = worryContent,
    status = status.raw,
    scheduledStartAt = scheduledStartAt.toIso(),
    scheduledEndAt = scheduledEndAt.toIso(),
)

private fun SessionSummaryDto.toDomain() = SessionSummary(
    id = id,
    worryId = worryId,
    worryContent = worryContent,
    status = SessionStatus.fromRaw(status),
    scheduledStartAt = scheduledStartAt.toLocalDateTime(),
    scheduledEndAt = scheduledEndAt.toLocalDateTime(),
)

private fun SessionRecord.toDto() = SessionRecordDto(
    id = id,
    recordType = recordType,
    contentText = contentText,
    mediaUrl = mediaUrl,
    createdAt = createdAt.toIso(),
)

private fun SessionRecordDto.toDomain() = SessionRecord(
    id = id,
    recordType = recordType,
    contentText = contentText,
    mediaUrl = mediaUrl,
    createdAt = createdAt.toLocalDateTime(),
)

private fun SessionDetail.toDto() = SessionDetailDto(
    id = id,
    worryId = worryId,
    worryContent = worryContent,
    worryMemo = worryMemo,
    themeCategory = themeCategory,
    status = status.raw,
    scheduledStartAt = scheduledStartAt.toIso(),
    scheduledEndAt = scheduledEndAt.toIso(),
    startedAt = startedAt?.toIso(),
    completedAt = completedAt?.toIso(),
    emotionScoreBefore = emotionScoreBefore,
    emotionScoreAfter = emotionScoreAfter,
    records = records.map { it.toDto() },
)

private fun SessionDetailDto.toDomain() = SessionDetail(
    id = id,
    worryId = worryId,
    worryContent = worryContent,
    worryMemo = worryMemo,
    themeCategory = themeCategory,
    status = SessionStatus.fromRaw(status),
    scheduledStartAt = scheduledStartAt.toLocalDateTime(),
    scheduledEndAt = scheduledEndAt.toLocalDateTime(),
    startedAt = startedAt?.toLocalDateTime(),
    completedAt = completedAt?.toLocalDateTime(),
    emotionScoreBefore = emotionScoreBefore,
    emotionScoreAfter = emotionScoreAfter,
    records = records.map { it.toDomain() },
)

private fun SessionDetail.toStatusResult() = SessionStatusResult(
    id = id,
    worryId = worryId,
    status = status,
    startedAt = startedAt,
    completedAt = completedAt,
    emotionScoreBefore = emotionScoreBefore,
    emotionScoreAfter = emotionScoreAfter,
)

/**
 * DataStore 기반 SessionRepository — API 연동 전, 로컬에서 예약/조회/수정이 실제로 반영되는 버전.
 * (이전 in-memory Fake 구현 대체, 바인딩은 [com.gominitta.android.di.AppModule] 참고)
 * 시드 데이터 없이 빈 상태로 시작 — 화면의 빈 상태 UI를 그대로 활용한다.
 */
class LocalSessionRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) : SessionRepository {

    override fun getSessions(status: SessionStatus?): Flow<List<SessionSummary>> =
        dataStore.data.map { prefs ->
            val summaries = decodeSummaries(prefs)
            if (status == null) summaries else summaries.filter { it.status == status }
        }

    override suspend fun getSessionDetail(sessionId: Long): SessionDetail =
        decodeDetails(dataStore.data.first())[sessionId]?.toDomain()
            ?: error("Session id=$sessionId 상세가 없습니다.")

    override suspend fun createSession(
        worryContent: String,
        worryMemo: String,
        scheduledStartAt: LocalDateTime,
        scheduledEndAt: LocalDateTime,
        emotionScoreBefore: Int?,
    ): SessionSummary {
        // 별도 Worry 엔티티가 없어서(이 앱은 걱정 내용을 세션에 직접 들고 있음) worryId는 id와 동일하게 발급.
        val id = System.currentTimeMillis()
        val summary = SessionSummary(
            id = id,
            worryId = id,
            worryContent = worryContent,
            status = SessionStatus.SCHEDULED,
            scheduledStartAt = scheduledStartAt,
            scheduledEndAt = scheduledEndAt,
        )
        val detail = SessionDetail(
            id = id,
            worryId = id,
            worryContent = worryContent,
            worryMemo = worryMemo,
            themeCategory = "미분류",
            status = SessionStatus.SCHEDULED,
            scheduledStartAt = scheduledStartAt,
            scheduledEndAt = scheduledEndAt,
            startedAt = null,
            completedAt = null,
            emotionScoreBefore = emotionScoreBefore,
            emotionScoreAfter = null,
            records = emptyList(),
        )
        dataStore.edit { prefs ->
            val summaries = decodeSummaries(prefs) + summary
            val details = decodeDetails(prefs) + (id to detail.toDto())
            prefs[SummariesKey] = json.encodeToString(summaries.map { it.toDto() })
            prefs[DetailsKey] = json.encodeToString(details)
        }
        return summary
    }

    override suspend fun updateSession(
        sessionId: Long,
        worryMemo: String,
        scheduledStartAt: LocalDateTime,
        scheduledEndAt: LocalDateTime,
    ): SessionSummary {
        var updatedSummary: SessionSummary? = null
        dataStore.edit { prefs ->
            val summaries = decodeSummaries(prefs).toMutableList()
            val index = summaries.indexOfFirst { it.id == sessionId }
            if (index == -1) error("Session id=$sessionId 요약이 없습니다.")
            val newSummary = summaries[index].copy(
                scheduledStartAt = scheduledStartAt,
                scheduledEndAt = scheduledEndAt,
            )
            summaries[index] = newSummary

            val details = decodeDetails(prefs).toMutableMap()
            val currentDetail = details[sessionId]?.toDomain() ?: error("Session id=$sessionId 상세가 없습니다.")
            details[sessionId] = currentDetail.copy(
                worryMemo = worryMemo,
                scheduledStartAt = scheduledStartAt,
                scheduledEndAt = scheduledEndAt,
            ).toDto()

            prefs[SummariesKey] = json.encodeToString(summaries.map { it.toDto() })
            prefs[DetailsKey] = json.encodeToString(details)
            updatedSummary = newSummary
        }
        return updatedSummary!!
    }

    override suspend fun deleteSession(sessionId: Long) {
        dataStore.edit { prefs ->
            val summaries = decodeSummaries(prefs).filterNot { it.id == sessionId }
            val details = decodeDetails(prefs) - sessionId
            prefs[SummariesKey] = json.encodeToString(summaries.map { it.toDto() })
            prefs[DetailsKey] = json.encodeToString(details)
        }
    }

    override suspend fun addRecord(
        sessionId: Long,
        recordType: String,
        contentText: String?,
        mediaUrl: String?,
    ): SessionRecord {
        val record = SessionRecord(
            id = System.currentTimeMillis(),
            recordType = recordType,
            contentText = contentText,
            mediaUrl = mediaUrl,
            createdAt = LocalDateTime.now(),
        )
        updateDetail(sessionId) { it.copy(records = it.records + record) }
        return record
    }

    override suspend fun startSession(sessionId: Long): SessionStatusResult =
        updateDetailAndSyncSummary(sessionId) { it.copy(status = SessionStatus.IN_PROGRESS, startedAt = LocalDateTime.now()) }
            .toStatusResult()

    override suspend fun completeSession(sessionId: Long, emotionScoreAfter: Int): SessionStatusResult =
        updateDetailAndSyncSummary(sessionId) {
            it.copy(
                status = SessionStatus.COMPLETED,
                completedAt = LocalDateTime.now(),
                emotionScoreAfter = emotionScoreAfter,
            )
        }.toStatusResult()

    private suspend fun updateDetail(sessionId: Long, transform: (SessionDetail) -> SessionDetail): SessionDetail {
        var updated: SessionDetail? = null
        dataStore.edit { prefs ->
            val details = decodeDetails(prefs).toMutableMap()
            val current = details[sessionId]?.toDomain() ?: error("Session id=$sessionId 상세가 없습니다.")
            updated = transform(current).also { details[sessionId] = it.toDto() }
            prefs[DetailsKey] = json.encodeToString(details)
        }
        return updated!!
    }

    /** [updateDetail]과 달리 status가 바뀌는 전이(시작/완료)에서는 목록에 쓰이는 summary의 status도 같이 맞춘다. */
    private suspend fun updateDetailAndSyncSummary(
        sessionId: Long,
        transform: (SessionDetail) -> SessionDetail,
    ): SessionDetail {
        var updated: SessionDetail? = null
        dataStore.edit { prefs ->
            val details = decodeDetails(prefs).toMutableMap()
            val current = details[sessionId]?.toDomain() ?: error("Session id=$sessionId 상세가 없습니다.")
            val newDetail = transform(current)
            details[sessionId] = newDetail.toDto()

            val summaries = decodeSummaries(prefs).toMutableList()
            val index = summaries.indexOfFirst { it.id == sessionId }
            if (index != -1) {
                summaries[index] = summaries[index].copy(status = newDetail.status)
            }

            prefs[DetailsKey] = json.encodeToString(details)
            prefs[SummariesKey] = json.encodeToString(summaries.map { it.toDto() })
            updated = newDetail
        }
        return updated!!
    }

    private fun decodeSummaries(prefs: Preferences): List<SessionSummary> =
        prefs[SummariesKey]
            ?.let { json.decodeFromString<List<SessionSummaryDto>>(it) }
            ?.map { it.toDomain() }
            ?: emptyList()

    private fun decodeDetails(prefs: Preferences): Map<Long, SessionDetailDto> =
        prefs[DetailsKey]?.let { json.decodeFromString<Map<Long, SessionDetailDto>>(it) } ?: emptyMap()
}
