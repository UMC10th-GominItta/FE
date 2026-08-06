package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.recipe.Recipe
import com.gominitta.android.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository,
) {
    suspend operator fun invoke(): List<Recipe> = repository.getRecipes()
}