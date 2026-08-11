package com.gominitta.android.domain.model.notification

import java.time.LocalDateTime

data class Notification(
    val id: Long,
    val type: NotificationType,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val readAt: LocalDateTime?,
    val createdAt: LocalDateTime,
)

enum class NotificationType(val raw: String) {
    WORRY_REMINDER("WORRY_REMINDER"),
    SESSION_REMINDER("SESSION_REMINDER"),
    SESSION_DONE("SESSION_DONE"),
    REPORT_READY("REPORT_READY"),
    SYSTEM("SYSTEM"),
    ;

    companion object {
        /** 서버가 향후 타입을 추가해도 목록 전체가 깨지지 않도록, 모르는 값은 SYSTEM으로 취급한다. */
        fun fromRaw(raw: String): NotificationType =
            entries.find { it.raw.equals(raw, ignoreCase = true) } ?: SYSTEM
    }
}

/** GET /api/v1/notifications 페이지 응답. */
data class NotificationPage(
    val notifications: List<Notification>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
)

data class NotificationSetting(
    val worryReminderEnabled: Boolean,
    val sessionStartEnabled: Boolean,
)
