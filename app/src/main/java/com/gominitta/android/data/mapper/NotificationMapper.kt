package com.gominitta.android.data.mapper

import com.gominitta.android.data.remote.dto.NotificationListResponseDto
import com.gominitta.android.data.remote.dto.NotificationResponseDto
import com.gominitta.android.data.remote.dto.NotificationSettingResponseDto
import com.gominitta.android.domain.model.notification.Notification
import com.gominitta.android.domain.model.notification.NotificationPage
import com.gominitta.android.domain.model.notification.NotificationSetting
import com.gominitta.android.domain.model.notification.NotificationType

fun NotificationResponseDto.toDomain() = Notification(
    id = notificationId,
    type = NotificationType.fromRaw(type),
    title = title,
    body = body,
    isRead = isRead,
    readAt = readAt.toLocalDateTimeOrNull(),
    createdAt = createdAt.toLocalDateTime(),
)

fun NotificationListResponseDto.toDomain() = NotificationPage(
    notifications = notifications.map { it.toDomain() },
    page = page,
    size = size,
    totalElements = totalElements,
    totalPages = totalPages,
    hasNext = hasNext,
)

fun NotificationSettingResponseDto.toDomain() = NotificationSetting(
    worryReminderEnabled = worryReminderEnabled,
    sessionStartEnabled = sessionStartEnabled,
)
