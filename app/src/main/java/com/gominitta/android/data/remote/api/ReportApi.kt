package com.gominitta.android.data.remote.api

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.dto.AnxietyGapResponse
import com.gominitta.android.data.remote.dto.WorryThemeResponse
import com.gominitta.android.data.remote.dto.WorryTimelineResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportApi {
    @GET("api/v1/reports/worry-themes")
    suspend fun getWorryThemes(
        @Query("period") period: String,
    ): ApiResponse<WorryThemeResponse>

    @GET("api/v1/reports/anxiety-gap")
    suspend fun getAnxietyGap(
        @Query("period") period: String,
    ): ApiResponse<AnxietyGapResponse>

    @GET("api/v1/reports/worry-timeline")
    suspend fun getWorryTimeline(
        @Query("period") period: String,
    ): ApiResponse<WorryTimelineResponse>
}
