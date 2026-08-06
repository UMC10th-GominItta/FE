package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.repository.RecipeRepository
import javax.inject.Inject

class CompleteRecipeLogUseCase @Inject constructor(
    private val repository: RecipeRepository,
) {
    suspend operator fun invoke(recipeLogId: Long) = repository.completeRecipeLog(recipeLogId)
}