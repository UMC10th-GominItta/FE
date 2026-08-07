package com.gominitta.android.data.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.RecipeApi
import com.gominitta.android.data.remote.api.RecipeLogApi
import com.gominitta.android.data.remote.dto.RecipeCreateRequest
import com.gominitta.android.data.remote.dto.RecipeLogCreateRequest
import com.gominitta.android.data.remote.dto.RecipeResponse
import com.gominitta.android.data.remote.dto.RecipeUpdateRequest
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.data.remote.safeApiCallUnit
import com.gominitta.android.domain.model.recipe.Recipe
import com.gominitta.android.domain.model.recipe.RecipeSummary
import com.gominitta.android.domain.repository.RecipeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealRecipeRepository @Inject constructor(
    private val recipeApi: RecipeApi,
    private val recipeLogApi: RecipeLogApi,
) : RecipeRepository {

    override suspend fun getRecipes(): List<Recipe> =
        unwrap(safeApiCall { recipeApi.getRecipes(scope = "mine") }).map { it.toDomain() }

    override suspend fun getRecommendedRecipes(): List<Recipe> =
        unwrap(safeApiCall { recipeApi.getRandomRecipes() }).map { it.toDomain() }

    override suspend fun getRecipe(recipeId: Long): Recipe =
        unwrap(safeApiCall { recipeApi.getRecipe(recipeId) }).toDomain()

    override suspend fun addRecipe(recipe: Recipe) {
        unwrap(
            safeApiCall {
                recipeApi.createRecipe(
                    RecipeCreateRequest(
                        title = recipe.title,
                        description = recipe.description,
                        estimatedMinutes = recipe.estimatedMinutes.toInt(),
                    ),
                )
            },
        )
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        unwrap(
            safeApiCall {
                recipeApi.updateRecipe(
                    recipeId = recipe.id,
                    request = RecipeUpdateRequest(
                        title = recipe.title,
                        description = recipe.description,
                        estimatedMinutes = recipe.estimatedMinutes.toInt(),
                    ),
                )
            },
        )
    }

    override suspend fun deleteRecipe(recipeId: Long) {
        unwrap(safeApiCallUnit { recipeApi.deleteRecipe(recipeId) })
    }

    override suspend fun getRecipeSummary(): RecipeSummary {
        val response = unwrap(safeApiCall { recipeLogApi.getSummary() })
        return RecipeSummary(response.todayCompletedCount, response.totalCompletedCount)
    }

    override suspend fun startRecipeLog(recipeId: Long): Long =
        unwrap(safeApiCall { recipeLogApi.createRecipeLog(RecipeLogCreateRequest(recipeId)) }).recipeLogId

    override suspend fun completeRecipeLog(recipeLogId: Long) {
        unwrap(safeApiCall { recipeLogApi.completeRecipeLog(recipeLogId) })
    }

    private fun RecipeResponse.toDomain() = Recipe(
        id = recipeId,
        title = title,
        description = description,
        estimatedMinutes = estimatedMinutes.toLong(),
    )

    private fun <T> unwrap(result: ApiResult<T>): T = when (result) {
        is ApiResult.Success -> result.data
        is ApiResult.Error -> throw IllegalStateException(result.message)
        is ApiResult.NetworkError -> throw result.cause
    }
}