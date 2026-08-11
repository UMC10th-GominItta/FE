package com.gominitta.android.data.repository

import com.gominitta.android.data.mapper.toDomain
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.NotificationApi
import com.gominitta.android.data.remote.dto.NotificationSettingUpdateRequestDto
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.domain.model.notification.Notification
import com.gominitta.android.domain.model.notification.NotificationPage
import com.gominitta.android.domain.model.notification.NotificationSetting
import com.gominitta.android.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApi,
) : NotificationRepository {

    override suspend fun getNotifications(page: Int, size: Int, sort: List<String>?): ApiResult<NotificationPage> =
        when (val result = safeApiCall { api.getNotifications(page, size, sort) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
        }

    override suspend fun readNotification(notificationId: Long): ApiResult<Notification> =
        when (val result = safeApiCall { api.readNotification(notificationId) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
        }

    override suspend fun getSettings(): ApiResult<NotificationSetting> =
        when (val result = safeApiCall { api.getSettings() }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
        }

    override suspend fun updateSettings(
        worryReminderEnabled: Boolean?,
        sessionStartEnabled: Boolean?,
    ): ApiResult<NotificationSetting> {
        val result = safeApiCall {
            api.updateSettings(
                NotificationSettingUpdateRequestDto(
                    worryReminderEnabled = worryReminderEnabled,
                    sessionStartEnabled = sessionStartEnabled,
                ),
            )
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
        }
    }
}
