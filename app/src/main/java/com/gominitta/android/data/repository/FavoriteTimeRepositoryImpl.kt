package com.gominitta.android.data.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.FavoriteTimeApi
import com.gominitta.android.data.remote.dto.FavoriteTimeDetailResponse
import com.gominitta.android.data.remote.dto.FavoriteTimeRequest
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.domain.model.mypage.FavoriteTime
import com.gominitta.android.domain.repository.FavoriteTimeRepository
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

class FavoriteTimeRepositoryImpl @Inject constructor(
    private val favoriteTimeApi: FavoriteTimeApi,
) : FavoriteTimeRepository {

    override suspend fun getFavoriteTimes(): List<FavoriteTime> =
        unwrap(safeApiCall { favoriteTimeApi.getFavoriteTimes() }).map { it.toDomain() }

    override suspend fun addFavoriteTime(
        label: String,
        startTime: LocalTime,
        endTime: LocalTime,
    ): FavoriteTime {
        val request = FavoriteTimeRequest(
            label = label,
            start_time = startTime.format(TIME_FORMATTER),
            end_time = endTime.format(TIME_FORMATTER),
        )
        val response = unwrap(safeApiCall { favoriteTimeApi.createFavoriteTime(request) })
        return FavoriteTime(
            id = response.favoriteTimeId,
            label = label,
            startTime = startTime,
            endTime = endTime,
        )
    }

    override suspend fun updateFavoriteTime(favoriteTime: FavoriteTime) {
        val request = FavoriteTimeRequest(
            label = favoriteTime.label,
            start_time = favoriteTime.startTime.format(TIME_FORMATTER),
            end_time = favoriteTime.endTime.format(TIME_FORMATTER),
        )
        unwrap(safeApiCall { favoriteTimeApi.updateFavoriteTime(favoriteTime.id, request) })
    }

    override suspend fun deleteFavoriteTime(favoriteTimeId: Long) {
        unwrap(safeApiCall { favoriteTimeApi.deleteFavoriteTime(favoriteTimeId) })
    }

    private fun FavoriteTimeDetailResponse.toDomain() = FavoriteTime(
        id = id,
        label = label,
        startTime = LocalTime.parse(start_time, TIME_FORMATTER),
        endTime = LocalTime.parse(end_time, TIME_FORMATTER),
    )

    private fun <T> unwrap(result: ApiResult<T>): T = when (result) {
        is ApiResult.Success -> result.data
        is ApiResult.Error -> throw IllegalStateException(result.message)
        is ApiResult.NetworkError -> throw result.cause
    }
}