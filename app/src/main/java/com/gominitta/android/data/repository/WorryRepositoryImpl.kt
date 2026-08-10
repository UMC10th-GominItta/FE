package com.gominitta.android.data.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.data.remote.worry.WorryApi
import com.gominitta.android.data.remote.worry.dto.WorryCreateRequest
import com.gominitta.android.domain.repository.WorryRepository
import javax.inject.Inject

class WorryRepositoryImpl @Inject constructor(
    private val api: WorryApi,
) : WorryRepository {

    override suspend fun createWorry(
        content: String,
        emotionScoreBefore: Int,
        scheduledStartAt: String,
        scheduledEndAt: String,
    ): ApiResult<Long> {
        val result = safeApiCall {
            api.createWorry(
                WorryCreateRequest(
                    content = content,
                    emotionScoreBefore = emotionScoreBefore,
                    scheduledStartAt = scheduledStartAt,
                    scheduledEndAt = scheduledEndAt,
                ),
            )
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.worryId)
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
        }
    }
}
