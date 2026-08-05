package com.gominitta.android.data.repository

import com.gominitta.android.domain.model.recipe.Recipe
import com.gominitta.android.domain.model.recipe.RecipeSummary
import com.gominitta.android.domain.repository.RecipeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyRecipeRepository @Inject constructor() : RecipeRepository {

    private val recipes = mutableListOf(
        Recipe(
            id = 1,
            title = "룸 스프레이 뿌리기",
            description = "좋아하는 향의 룸 스프레이를 방 안 곳곳에 뿌리고, 눈을 감고 깊게 향을 들이마시며 5분간 휴식합니다.",
            estimatedMinutes = 5,
        ),
        Recipe(
            id = 2,
            title = "찬물 한 컵 마시기",
            description = "마음을 진정시켜주는 찬물을 한 컵 마십니다.",
            estimatedMinutes = 1,
        ),
    )

    override suspend fun getRecipes(): List<Recipe> = recipes.toList()

    override suspend fun getRecommendedRecipes(): List<Recipe> = recipes.take(3)

    override suspend fun addRecipe(recipe: Recipe) {
        recipes.add(recipe)
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        val index = recipes.indexOfFirst { it.id == recipe.id }
        if (index != -1) recipes[index] = recipe
    }

    override suspend fun deleteRecipe(recipeId: Long) {
        recipes.removeAll { it.id == recipeId }
    }

    override suspend fun getRecipeSummary(): RecipeSummary =
        RecipeSummary(todayCompletedCount = 1, totalCompletedCount = 3)
}