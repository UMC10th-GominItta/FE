package com.gominitta.android.data.remote.api

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.dto.WorryThemeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportApi {
    @GET("api/v1/reports/worry-themes")
    suspend fun getWorryThemes(
        @Query("period") period: String,
    ): ApiResponse<WorryThemeResponse>
}
