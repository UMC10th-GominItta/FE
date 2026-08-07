package com.gominitta.android.data.remote.api

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.dto.RecipeCreateRequest
import com.gominitta.android.data.remote.dto.RecipeResponse
import com.gominitta.android.data.remote.dto.RecipeUpdateRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {
    @GET("api/v1/recipes")
    suspend fun getRecipes(@Query("scope") scope: String = "mine"): ApiResponse<List<RecipeResponse>>

    @GET("api/v1/recipes/random")
    suspend fun getRandomRecipes(): ApiResponse<List<RecipeResponse>>

    @GET("api/v1/recipes/{recipeId}")
    suspend fun getRecipe(@Path("recipeId") recipeId: Long): ApiResponse<RecipeResponse>

    @POST("api/v1/recipes")
    suspend fun createRecipe(@Body request: RecipeCreateRequest): ApiResponse<RecipeResponse>

    @PATCH("api/v1/recipes/{recipeId}")
    suspend fun updateRecipe(
        @Path("recipeId") recipeId: Long,
        @Body request: RecipeUpdateRequest,
    ): ApiResponse<RecipeResponse>

    @DELETE("api/v1/recipes/{recipeId}")
    suspend fun deleteRecipe(@Path("recipeId") recipeId: Long): ApiResponse<Unit?>
}