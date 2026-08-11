package com.gominitta.android.domain.usecase

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.repository.WorryRepository
import javax.inject.Inject

/** 예약된 걱정 수정(C105) 저장 — 내용/예약 시간만 수정 가능하다. */
class UpdateWorryUseCase @Inject constructor(
    private val repository: WorryRepository,
) {
    suspend operator fun invoke(
        worryId: Long,
        title: String,
        content: String,
        scheduledStartAt: String,
        scheduledEndAt: String,
    ): ApiResult<Long> = repository.updateWorry(worryId, title, content, scheduledStartAt, scheduledEndAt)
}
