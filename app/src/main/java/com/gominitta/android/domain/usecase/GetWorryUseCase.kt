package com.gominitta.android.domain.usecase

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.worry.Worry
import com.gominitta.android.domain.repository.WorryRepository
import javax.inject.Inject

/** 예약된 걱정 수정(C105) 진입 시 기존 내용을 불러온다. */
class GetWorryUseCase @Inject constructor(
    private val repository: WorryRepository,
) {
    suspend operator fun invoke(worryId: Long): ApiResult<Worry> = repository.getWorry(worryId)
}
