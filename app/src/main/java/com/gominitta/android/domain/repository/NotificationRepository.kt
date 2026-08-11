package com.gominitta.android.domain.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.notification.Notification
import com.gominitta.android.domain.model.notification.NotificationPage
import com.gominitta.android.domain.model.notification.NotificationSetting

interface NotificationRepository {
    suspend fun getNotifications(page: Int, size: Int, sort: List<String>? = null): ApiResult<NotificationPage>

    suspend fun readNotification(notificationId: Long): ApiResult<Notification>

    suspend fun getSettings(): ApiResult<NotificationSetting>

    /** 수정할 필드만 넘기면 된다 — null은 "변경 없음". */
    suspend fun updateSettings(
        worryReminderEnabled: Boolean?,
        sessionStartEnabled: Boolean?,
    ): ApiResult<NotificationSetting>
}
