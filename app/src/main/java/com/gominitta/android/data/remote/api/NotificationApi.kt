package com.gominitta.android.data.remote.api

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.dto.NotificationListResponseDto
import com.gominitta.android.data.remote.dto.NotificationResponseDto
import com.gominitta.android.data.remote.dto.NotificationSettingResponseDto
import com.gominitta.android.data.remote.dto.NotificationSettingUpdateRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

/** 알림 · 알림 설정 엔드포인트. */
interface NotificationApi {
    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
    ): ApiResponse<NotificationListResponseDto>

    @PATCH("api/v1/notifications/{notificationId}")
    suspend fun readNotification(
        @Path("notificationId") notificationId: Long,
    ): ApiResponse<NotificationResponseDto>

    @GET("api/v1/notifications/settings")
    suspend fun getSettings(): ApiResponse<NotificationSettingResponseDto>

    @PATCH("api/v1/notifications/settings")
    suspend fun updateSettings(
        @Body body: NotificationSettingUpdateRequestDto,
    ): ApiResponse<NotificationSettingResponseDto>
}
