package com.gominitta.android.domain.usecase

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.repository.WorryRepository
import javax.inject.Inject

/** 한 줄 보태기(B104) 저장 — 걱정 내용에 한 줄을 덧붙인다. */
class AddWorryContentUseCase @Inject constructor(
    private val repository: WorryRepository,
) {
    suspend operator fun invoke(worryId: Long, content: String): ApiResult<Long> =
        repository.addWorryContent(worryId, content)
}
