package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class SessionRecordResponseDto(
    val id: Long,
    val sessionId: Long,
    val recordType: String,
    val contentText: String? = null,
    val mediaUrl: String? = null,
    val createdAt: String,
    val updatedAt: String? = null,
)

/** POST /api/v1/sessions/{id}/records 요청 바디 — TextRecordCreateRequestDTO. */
@Serializable
data class TextRecordCreateRequestDto(
    val contentText: String,
)

/** PATCH /api/v1/sessions/{id}/records/{recordId} 요청 바디 — RecordUpdateRequestDTO. */
@Serializable
data class RecordUpdateRequestDto(
    val contentText: String,
)
