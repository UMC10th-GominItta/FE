package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.recipe.Recipe
import com.gominitta.android.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository,
) {
    suspend operator fun invoke(recipeId: Long): Recipe = repository.getRecipe(recipeId)
}