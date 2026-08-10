package com.gominitta.android.domain.usecase

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.repository.WorryRepository
import javax.inject.Inject

/** 예약된 걱정 수정(C105) 화면에서 삭제. */
class DeleteWorryUseCase @Inject constructor(
    private val repository: WorryRepository,
) {
    suspend operator fun invoke(worryId: Long): ApiResult<Long> = repository.deleteWorry(worryId)
}
