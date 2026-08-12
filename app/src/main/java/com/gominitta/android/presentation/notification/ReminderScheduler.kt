package com.gominitta.android.presentation.notification

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 걱정 리마인드(예약 시작 20분 전 — 백엔드 스케줄러와 동일한 타이밍) · 마음 세션 시작
 * 로컬 알림을 예약/취소한다.
 *
 * 걱정과 세션은 1:1이고(백엔드가 걱정 생성 시 같은 시간으로 세션도 같이 만든다),
 * 걱정 생성 응답엔 세션 id가 안 와서 두 리마인드 모두 worryId로 식별한다.
 */
@Singleton
class ReminderScheduler @Inject constructor(
    private val workManager: WorkManager,
) {
    fun scheduleWorryReminder(worryId: Long, scheduledStartAt: LocalDateTime) {
        schedule(
            uniqueName = worryReminderWorkName(worryId),
            fireAt = scheduledStartAt.minusMinutes(WORRY_REMINDER_MINUTES_BEFORE),
            type = ReminderWorker.TYPE_WORRY,
            title = "곧 마주할 시간이에요",
            body = "예약한 걱정을 마주할 시간이 다가오고 있어요.",
            notificationId = worryReminderNotificationId(worryId),
            worryId = worryId,
        )
    }

    fun scheduleSessionStartAlarm(worryId: Long, scheduledStartAt: LocalDateTime) {
        schedule(
            uniqueName = sessionStartWorkName(worryId),
            fireAt = scheduledStartAt,
            type = ReminderWorker.TYPE_SESSION,
            title = "마음 세션 시작 시간이에요",
            body = "지금 예약한 마음 세션을 시작해보세요.",
            notificationId = sessionStartNotificationId(worryId),
            worryId = worryId,
        )
    }

    /** 걱정 수정으로 시간이 바뀌면 두 리마인드를 한 번에 다시 예약한다. */
    fun rescheduleAll(worryId: Long, scheduledStartAt: LocalDateTime) {
        scheduleWorryReminder(worryId, scheduledStartAt)
        scheduleSessionStartAlarm(worryId, scheduledStartAt)
    }

    /** 걱정 삭제 시 두 리마인드 예약을 모두 취소한다. */
    fun cancelReminders(worryId: Long) {
        workManager.cancelUniqueWork(worryReminderWorkName(worryId))
        workManager.cancelUniqueWork(sessionStartWorkName(worryId))
    }

    /**
     * 로그아웃 — 이전 계정의 예약이 남아 울리지 않도록 전부 취소한다.
     * WorkManager가 자동으로 붙이는 클래스명 태그를 쓴다 — 우리 태그를 달기 전 버전이
     * 예약해둔 작업까지 걷어내야 하기 때문이다.
     */
    fun cancelAll() {
        workManager.cancelAllWorkByTag(ReminderWorker::class.java.name)
    }

    private fun schedule(
        uniqueName: String,
        fireAt: LocalDateTime,
        type: String,
        title: String,
        body: String,
        notificationId: Int,
        worryId: Long,
    ) {
        val delay = Duration.between(LocalDateTime.now(), fireAt)
        if (delay.isNegative) {
            // 이미 지난 시각 — 새로 예약하지 않고, 걸려있던 이전 예약이 있으면 정리한다.
            workManager.cancelUniqueWork(uniqueName)
            return
        }
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(
                Data.Builder()
                    .putString(ReminderWorker.KEY_TYPE, type)
                    .putString(ReminderWorker.KEY_TITLE, title)
                    .putString(ReminderWorker.KEY_BODY, body)
                    .putInt(ReminderWorker.KEY_NOTIFICATION_ID, notificationId)
                    .putLong(ReminderWorker.KEY_WORRY_ID, worryId)
                    .build(),
            )
            .build()
        workManager.enqueueUniqueWork(uniqueName, ExistingWorkPolicy.REPLACE, request)
    }

    private fun worryReminderWorkName(worryId: Long) = "worry_reminder_$worryId"

    private fun sessionStartWorkName(worryId: Long) = "session_start_$worryId"

    private fun worryReminderNotificationId(worryId: Long) = "wr$worryId".hashCode()

    private fun sessionStartNotificationId(worryId: Long) = "ss$worryId".hashCode()

    private companion object {
        const val WORRY_REMINDER_MINUTES_BEFORE = 20L
    }
}
