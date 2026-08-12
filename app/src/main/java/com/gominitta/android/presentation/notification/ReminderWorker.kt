package com.gominitta.android.presentation.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.usecase.GetNotificationSettingUseCase
import com.gominitta.android.domain.usecase.GetSessionListUseCase
import com.gominitta.android.domain.usecase.GetWorryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

/** 예약된 시각에 실행돼 알림 설정을 확인하고 리마인드 알림을 띄운다. */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getNotificationSetting: GetNotificationSettingUseCase,
    private val getWorry: GetWorryUseCase,
    private val getSessionList: GetSessionListUseCase,
    private val notifier: ReminderNotifier,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val body = inputData.getString(KEY_BODY) ?: return Result.failure()
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, id.hashCode())
        val worryId = inputData.getLong(KEY_WORRY_ID, NO_WORRY_ID)

        if (isReminderEnabled(type) && isStillPending(worryId)) {
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

    /**
     * 예약은 기기에 남지만 걱정은 서버에서 사라질 수 있다(삭제·계정 전환·다른 기기에서 정리).
     * 그래서 띄우기 직전에 아직 유효한 걱정인지, 세션이 이미 끝나지 않았는지 확인한다.
     *
     * "걱정이 없다"가 확실할 때만 취소한다 — 토큰 만료(401)나 서버 오류(5xx)를 삭제로
     * 오해하면 멀쩡한 리마인드를 조용히 놓친다.
     */
    private suspend fun isStillPending(worryId: Long): Boolean {
        if (worryId == NO_WORRY_ID) return true
        when (val result = getWorry(worryId)) {
            is ApiResult.Success -> Unit
            is ApiResult.Error -> if (result.isNotFound()) return false
            is ApiResult.NetworkError -> return true
        }
        // 완료 세션은 기본 목록에 안 와서 따로 물어봐야 한다.
        val completed = try {
            getSessionList(SessionStatus.COMPLETED)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return true
        }
        return completed.none { it.worryId == worryId }
    }

    /** 서버 envelope 코드가 없으면 safeApiCall 이 "HTTP_404" 로 채운다. */
    private fun ApiResult.Error.isNotFound(): Boolean =
        code == "HTTP_404" || code.contains("NOT_FOUND", ignoreCase = true)

    companion object {
        const val TYPE_WORRY = "worry"
        const val TYPE_SESSION = "session"
        const val KEY_TYPE = "type"
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_NOTIFICATION_ID = "notification_id"
        const val KEY_WORRY_ID = "worry_id"

        /** 이 키가 없던 예전 버전이 예약해둔 작업 — 검증 없이 그대로 띄운다. */
        private const val NO_WORRY_ID = -1L
    }
}
