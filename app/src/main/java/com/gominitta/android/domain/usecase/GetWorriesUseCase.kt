package com.gominitta.android.domain.usecase

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.worry.Worry
import com.gominitta.android.domain.repository.WorryRepository
import javax.inject.Inject

/** 걱정 전체 목록 조회 — 세션의 title/content 스냅샷이 걱정 수정에 반영 안 되는 걸 우회할 때 쓴다. */
class GetWorriesUseCase @Inject constructor(
    private val repository: WorryRepository,
) {
    suspend operator fun invoke(): ApiResult<List<Worry>> = repository.getWorries()
}
