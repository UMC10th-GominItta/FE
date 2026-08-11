package com.gominitta.android.data.repository

import com.gominitta.android.data.mapper.toDomain
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.data.remote.worry.WorryApi
import com.gominitta.android.data.remote.worry.dto.WorryCreateRequest
import com.gominitta.android.data.remote.worry.dto.WorryCreateResponse
import com.gominitta.android.data.remote.worry.dto.WorryUpdateRequest
import com.gominitta.android.domain.model.worry.Worry
import com.gominitta.android.domain.repository.WorryRepository
import javax.inject.Inject

class WorryRepositoryImpl @Inject constructor(
    private val api: WorryApi,
) : WorryRepository {

    override suspend fun createWorry(
        title: String,
        content: String,
        emotionScoreBefore: Int,
        scheduledStartAt: String,
        scheduledEndAt: String,
    ): ApiResult<Long> {
        val result = safeApiCall {
            api.createWorry(
                WorryCreateRequest(
                    title = title,
                    content = content,
                    emotionScoreBefore = emotionScoreBefore,
                    scheduledStartAt = scheduledStartAt,
                    scheduledEndAt = scheduledEndAt,
                ),
            )
        }
        return result.mapWorryId()
    }

    override suspend fun getWorry(worryId: Long): ApiResult<Worry> =
        when (val result = safeApiCall { api.getWorry(worryId) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
        }

    override suspend fun updateWorry(
        worryId: Long,
        title: String,
        content: String,
        scheduledStartAt: String,
        scheduledEndAt: String,
    ): ApiResult<Long> {
        val result = safeApiCall {
            api.updateWorry(
                worryId,
                WorryUpdateRequest(
                    title = title,
                    content = content,
                    scheduledStartAt = scheduledStartAt,
                    scheduledEndAt = scheduledEndAt,
                ),
            )
        }
        return result.mapWorryId()
    }

    override suspend fun deleteWorry(worryId: Long): ApiResult<Long> =
        safeApiCall { api.deleteWorry(worryId) }.mapWorryId()

    private fun ApiResult<WorryCreateResponse>.mapWorryId(): ApiResult<Long> =
        when (this) {
            is ApiResult.Success -> ApiResult.Success(data.worryId)
            is ApiResult.Error -> this
            is ApiResult.NetworkError -> this
        }
}
