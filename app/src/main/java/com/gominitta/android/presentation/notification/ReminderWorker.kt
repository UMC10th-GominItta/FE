package com.gominitta.android.presentation.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.usecase.GetNotificationSettingUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** 예약된 시각에 실행돼 알림 설정을 확인하고 리마인드 알림을 띄운다. */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getNotificationSetting: GetNotificationSettingUseCase,
    private val notifier: ReminderNotifier,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val body = inputData.getString(KEY_BODY) ?: return Result.failure()
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, id.hashCode())

        if (isReminderEnabled(type)) {
            notifier.show(notificationId, title, body)
        }
        return Result.success()
    }

    /** 설정 조회 실패 시엔 백엔드 기본값(설정 없으면 항상 켜짐)과 동일하게 켜진 것으로 본다. */
    private suspend fun isReminderEnabled(type: String): Boolean {
        val setting = when (val result = getNotificationSetting()) {
            is ApiResult.Success -> result.data
            is ApiResult.Error, is ApiResult.NetworkError -> return true
        }
        return when (type) {
            TYPE_WORRY -> setting.worryReminderEnabled
            TYPE_SESSION -> setting.sessionStartEnabled
            else -> true
        }
    }

    companion object {
        const val TYPE_WORRY = "worry"
        const val TYPE_SESSION = "session"
        const val KEY_TYPE = "type"
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_NOTIFICATION_ID = "notification_id"
    }
}
