package com.gominitta.android.data.remote.worry

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.worry.dto.WorryCreateRequest
import com.gominitta.android.data.remote.worry.dto.WorryCreateResponse
import com.gominitta.android.data.remote.worry.dto.WorryDetailResponse
import com.gominitta.android.data.remote.worry.dto.WorryUpdateRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface WorryApi {
    @POST("api/v1/worries")
    suspend fun createWorry(@Body req: WorryCreateRequest): ApiResponse<WorryCreateResponse>

    @GET("api/v1/worries/{worryId}")
    suspend fun getWorry(@Path("worryId") worryId: Long): ApiResponse<WorryDetailResponse>

    /** [worryId] 걱정의 내용/예약 시간을 수정한다 — 불안도 게이지(emotionScoreBefore)는 수정 대상이 아니다. */
    @PATCH("api/v1/worries/{worryId}")
    suspend fun updateWorry(
        @Path("worryId") worryId: Long,
        @Body req: WorryUpdateRequest,
    ): ApiResponse<WorryCreateResponse>

    @DELETE("api/v1/worries/{worryId}")
    suspend fun deleteWorry(@Path("worryId") worryId: Long): ApiResponse<WorryCreateResponse>
}
