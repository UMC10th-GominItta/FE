package com.gominitta.android.domain.repository

import com.gominitta.android.domain.model.recipe.Recipe
import com.gominitta.android.domain.model.recipe.RecipeSummary

interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>
    suspend fun getRecommendedRecipes(): List<Recipe>
    suspend fun getRecipe(recipeId: Long): Recipe   // 추가

    suspend fun addRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipeId: Long)
    suspend fun getRecipeSummary(): RecipeSummary
    suspend fun completeRecipe(recipeId: Long): RecipeSummary   // 추가

}