package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

/** GET /api/v1/sessions — SessionListResponseDTO */
@Serializable
data class SessionListResponseDto(
    val id: Long,
    val worryId: Long,
    val worryContent: String,
    val status: String,
    val scheduledStartAt: String,
    val scheduledEndAt: String,
)

/** GET /api/v1/sessions/{sessionId} — SessionDetailResponseDTO */
@Serializable
data class SessionDetailResponseDto(
    val id: Long,
    val worryId: Long,
    val worryContent: String,
    val themeCategory: String? = null,
    val status: String,
    val scheduledStartAt: String,
    val scheduledEndAt: String,
    val startedAt: String? = null,
    val completedAt: String? = null,
    val emotionScoreBefore: Int? = null,
    val emotionScoreAfter: Int? = null,
    val records: List<SessionRecordInfoDto> = emptyList(),
)

/**
 * 세션 상세 안에 중첩된 기록 — SessionRecordInfo.
 * 목록 API 의 [SessionRecordResponseDto] 와 달리 sessionId·updatedAt 이 없다.
 */
@Serializable
data class SessionRecordInfoDto(
    val id: Long,
    val recordType: String,
    val contentText: String? = null,
    val mediaUrl: String? = null,
    val createdAt: String,
)

/** PATCH /api/v1/sessions/{id} — SessionStatusChangeResponseDTO */
@Serializable
data class SessionStatusChangeResponseDto(
    val id: Long,
    val worryId: Long,
    val status: String,
    val startedAt: String? = null,
    val completedAt: String? = null,
    val emotionScoreBefore: Int? = null,
    val emotionScoreAfter: Int? = null,
)

/**
 * PATCH /api/v1/sessions/{id} 요청 바디 — SessionStatusChangeRequestDTO.
 * [emotionScoreAfter] 는 status = "completed" 일 때만 필수(0~10).
 */
@Serializable
data class SessionStatusChangeRequestDto(
    val status: String,
    val emotionScoreAfter: Int? = null,
)
