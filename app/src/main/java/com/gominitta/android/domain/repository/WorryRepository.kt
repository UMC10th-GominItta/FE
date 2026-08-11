package com.gominitta.android.domain.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.worry.Worry

/**
 * 데이터 계층 seam for 걱정 예약(B101~B103) · 수정/삭제(C105), DOMAIN 계층에 선언(의존성 역전).
 * 구현체는 [com.gominitta.android.data.repository.WorryRepositoryImpl].
 */
interface WorryRepository {
    suspend fun createWorry(
        title: String,
        content: String,
        emotionScoreBefore: Int,
        scheduledStartAt: String,
        scheduledEndAt: String,
    ): ApiResult<Long>

    suspend fun getWorry(worryId: Long): ApiResult<Worry>

    /** 불안도 게이지(emotionScoreBefore)는 수정 대상이 아니다. */
    suspend fun updateWorry(
        worryId: Long,
        title: String,
        content: String,
        scheduledStartAt: String,
        scheduledEndAt: String,
    ): ApiResult<Long>

    suspend fun deleteWorry(worryId: Long): ApiResult<Long>

    suspend fun addWorryContent(worryId: Long, content: String): ApiResult<Long>
}
