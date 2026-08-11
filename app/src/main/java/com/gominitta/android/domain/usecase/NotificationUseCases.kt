package com.gominitta.android.domain.usecase

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.notification.Notification
import com.gominitta.android.domain.model.notification.NotificationPage
import com.gominitta.android.domain.model.notification.NotificationSetting
import com.gominitta.android.domain.repository.NotificationRepository
import javax.inject.Inject

/** 알림 목록(페이지네이션). */
class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(page: Int = 0, size: Int = 20, sort: List<String>? = null): ApiResult<NotificationPage> =
        repository.getNotifications(page, size, sort)
}

/** 알림 항목 탭 → 읽음 처리. */
class ReadNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(notificationId: Long): ApiResult<Notification> =
        repository.readNotification(notificationId)
}

class GetNotificationSettingUseCase @Inject constructor(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(): ApiResult<NotificationSetting> = repository.getSettings()
}

/** 토글 하나만 바꿀 때도 나머지 파라미터는 null로 두면 된다(부분 수정). */
class UpdateNotificationSettingUseCase @Inject constructor(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(
        worryReminderEnabled: Boolean? = null,
        sessionStartEnabled: Boolean? = null,
    ): ApiResult<NotificationSetting> = repository.updateSettings(worryReminderEnabled, sessionStartEnabled)
}
