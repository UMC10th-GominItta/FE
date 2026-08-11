package com.gominitta.android.data.mapper

import com.gominitta.android.data.remote.dto.SessionDetailResponseDto
import com.gominitta.android.data.remote.dto.SessionListResponseDto
import com.gominitta.android.data.remote.dto.SessionRecordInfoDto
import com.gominitta.android.data.remote.dto.SessionRecordResponseDto
import com.gominitta.android.domain.model.session.RecordType
import com.gominitta.android.domain.model.session.Session
import com.gominitta.android.domain.model.session.SessionRecord
import com.gominitta.android.domain.model.session.SessionStatus
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

fun SessionListResponseDto.toDomain() = Session(
    id = id,
    worryId = worryId,
    worryTitle = worryTitle,
    worryContent = worryContent,
    scheduledStartAt = scheduledStartAt.toLocalDateTime(),
    scheduledEndAt = scheduledEndAt.toLocalDateTime(),
    status = SessionStatus.fromRaw(status),
)

fun SessionDetailResponseDto.toDomain() = Session(
    id = id,
    worryId = worryId,
    worryTitle = worryTitle,
    worryContent = worryContent,
    scheduledStartAt = scheduledStartAt.toLocalDateTime(),
    scheduledEndAt = scheduledEndAt.toLocalDateTime(),
    status = SessionStatus.fromRaw(status),
    emotionScoreBefore = emotionScoreBefore,
    themeCategory = themeCategory,
    startedAt = startedAt.toLocalDateTimeOrNull(),
    completedAt = completedAt.toLocalDateTimeOrNull(),
    emotionScoreAfter = emotionScoreAfter,
    records = records.map { it.toDomain() },
)

fun SessionRecordInfoDto.toDomain() = SessionRecord(
    id = id,
    recordType = RecordType.fromRaw(recordType),
    contentText = contentText.orEmpty(),
    mediaUrl = mediaUrl,
    createdAt = createdAt.toLocalDateTime(),
)

fun SessionRecordResponseDto.toDomain() = SessionRecord(
    id = id,
    recordType = RecordType.fromRaw(recordType),
    contentText = contentText.orEmpty(),
    mediaUrl = mediaUrl,
    createdAt = createdAt.toLocalDateTime(),
)


/**
 * 서버는 "2026-05-27T22:00:00" 형태의 ISO-8601 로컬 시각을 준다.
 * 값이 비어 있으면 파싱 실패 대신 null 을 돌려준다 — 서버가 nullable 필드에
 * 문자열 "null" 을 담아 보내는 케이스도 방어한다.
 */
internal fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
    if (this.isNullOrBlank() || this == "null") return null
    return try {
        LocalDateTime.parse(this)
    } catch (e: DateTimeParseException) {
        null
    }
}

/** 필수 시각 필드용 — 파싱 실패는 서버 계약 위반이므로 예외를 그대로 던진다. */
internal fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this)
