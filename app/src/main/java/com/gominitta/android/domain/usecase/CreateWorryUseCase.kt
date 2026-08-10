package com.gominitta.android.domain.usecase

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.repository.WorryRepository
import javax.inject.Inject

/** 걱정 예약(B101~B103) 저장 — 입력된 걱정을 서버에 예약한다. */
class CreateWorryUseCase @Inject constructor(
    private val repository: WorryRepository,
) {
    suspend operator fun invoke(
        content: String,
        emotionScoreBefore: Int,
        scheduledStartAt: String,
        scheduledEndAt: String,
    ): ApiResult<Long> = repository.createWorry(content, emotionScoreBefore, scheduledStartAt, scheduledEndAt)
}
