package com.gominitta.android.data.repository

import android.util.Log
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
private const val TAG = "FavoriteTimeRepo"

class FavoriteTimeRepositoryImpl @Inject constructor(
    private val favoriteTimeApi: FavoriteTimeApi,
) : FavoriteTimeRepository {

    override suspend fun getFavoriteTimes(): List<FavoriteTime> =
        unwrap(safeApiCall { favoriteTimeApi.getFavoriteTimes() }).map { it.toDomain() }
            .also { list ->
                // 서버가 실제로 어떤 label을 들고 있는지 확인용
                Log.d(TAG, "getFavoriteTimes() -> ${list.map { it.id to it.label }}")
            }

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

        // 1) 클라이언트가 실제로 뭘 보내는지
        Log.d(TAG, "updateFavoriteTime(id=${favoriteTime.id}) request=$request")

        val result = safeApiCall { favoriteTimeApi.updateFavoriteTime(favoriteTime.id, request) }

        // 2) 서버가 뭐라고 응답했는지 (성공/실패 여부, 바디)
        Log.d(TAG, "updateFavoriteTime(id=${favoriteTime.id}) result=$result")

        unwrap(result)

        // 3) PATCH 직후 GET 다시 찍어서, 서버에 실제로 반영됐는지 확인
        //    (label만 롤백되고 시간만 반영되는지 여기서 바로 보임)
        val after = unwrap(safeApiCall { favoriteTimeApi.getFavoriteTimes() })
            .firstOrNull { it.id == favoriteTime.id }
        Log.d(TAG, "updateFavoriteTime(id=${favoriteTime.id}) after PATCH, GET returns label=${after?.label}")
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