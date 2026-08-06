package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.recipe.RecipeSummary
import com.gominitta.android.domain.repository.RecipeRepository
import javax.inject.Inject

class CompleteRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository,
) {
    suspend operator fun invoke(recipeId: Long): RecipeSummary = repository.completeRecipe(recipeId)
}