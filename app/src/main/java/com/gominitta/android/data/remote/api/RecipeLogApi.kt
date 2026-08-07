package com.gominitta.android.data.remote.api

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.dto.RecipeLogCompleteResponse
import com.gominitta.android.data.remote.dto.RecipeLogCreateRequest
import com.gominitta.android.data.remote.dto.RecipeLogResponse
import com.gominitta.android.data.remote.dto.RecipeLogSummaryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RecipeLogApi {
    @POST("api/v1/recipe-logs")
    suspend fun createRecipeLog(@Body request: RecipeLogCreateRequest): ApiResponse<RecipeLogResponse>

    @PATCH("api/v1/recipe-logs/{recipeLogId}")
    suspend fun completeRecipeLog(@Path("recipeLogId") recipeLogId: Long): ApiResponse<RecipeLogCompleteResponse>

    @GET("api/v1/recipe-logs/summary")
    suspend fun getSummary(): ApiResponse<RecipeLogSummaryResponse>
}