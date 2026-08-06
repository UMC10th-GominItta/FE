package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.repository.RecipeRepository
import javax.inject.Inject

class DeleteRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository,
) {
    suspend operator fun invoke(recipeId: Long) = repository.deleteRecipe(recipeId)
}