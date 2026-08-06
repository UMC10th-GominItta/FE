package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.recipe.Recipe
import com.gominitta.android.domain.repository.RecipeRepository
import javax.inject.Inject

class CreateRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository,
) {
    suspend operator fun invoke(recipe: Recipe) = repository.addRecipe(recipe)
}