package com.gominitta.android.data.remote.api

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.dto.FavoriteTimeDetailResponse
import com.gominitta.android.data.remote.dto.FavoriteTimeRequest
import com.gominitta.android.data.remote.dto.FavoriteTimeResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteTimeApi {
    @GET("api/v1/favoriteTimeSlots")
    suspend fun getFavoriteTimes(): ApiResponse<List<FavoriteTimeDetailResponse>>

    @POST("api/v1/favoriteTimeSlots")
    suspend fun createFavoriteTime(@Body request: FavoriteTimeRequest): ApiResponse<FavoriteTimeResponse>

    @PATCH("api/v1/favoriteTimeSlots/{favoriteTimeSlotsId}")
    suspend fun updateFavoriteTime(
        @Path("favoriteTimeSlotsId") id: Long,
        @Body request: FavoriteTimeRequest,
    ): ApiResponse<FavoriteTimeResponse>

    @DELETE("api/v1/favoriteTimeSlots/{favoriteTimeSlotsId}")
    suspend fun deleteFavoriteTime(@Path("favoriteTimeSlotsId") id: Long): ApiResponse<FavoriteTimeResponse>
}