package com.gominitta.android.domain.repository

import com.gominitta.android.data.remote.ApiResult

/**
 * 데이터 계층 seam for 걱정 예약(B101~B103), DOMAIN 계층에 선언(의존성 역전).
 * 구현체는 [com.gominitta.android.data.repository.WorryRepositoryImpl].
 */
interface WorryRepository {
    suspend fun createWorry(
        content: String,
        emotionScoreBefore: Int,
        scheduledStartAt: String,
        scheduledEndAt: String,
    ): ApiResult<Long>
}
