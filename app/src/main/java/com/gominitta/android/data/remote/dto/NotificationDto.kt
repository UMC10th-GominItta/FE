package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

/** GET /api/v1/notifications 항목 — NotificationResponseDTO. */
@Serializable
data class NotificationResponseDto(
    val notificationId: Long,
    val type: String,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val readAt: String? = null,
    val createdAt: String,
)

/** GET /api/v1/notifications 응답 — NotificationListResponseDTO. */
@Serializable
data class NotificationListResponseDto(
    val notifications: List<NotificationResponseDto>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
)

/** GET/PATCH /api/v1/notifications/settings 응답 — NotificationSettingResponseDTO. */
@Serializable
data class NotificationSettingResponseDto(
    val worryReminderEnabled: Boolean,
    val sessionStartEnabled: Boolean,
)

/**
 * PATCH /api/v1/notifications/settings 요청 바디 — NotificationSettingUpdateRequestDTO.
 * 수정할 필드만 포함하면 된다(UserUpdateRequestDTO와 동일한 부분 수정 패턴).
 */
@Serializable
data class NotificationSettingUpdateRequestDto(
    val worryReminderEnabled: Boolean? = null,
    val sessionStartEnabled: Boolean? = null,
)
