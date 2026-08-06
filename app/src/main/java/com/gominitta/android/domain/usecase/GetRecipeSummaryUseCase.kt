package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.recipe.RecipeSummary
import com.gominitta.android.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipeSummaryUseCase @Inject constructor(
    private val repository: RecipeRepository,
) {
    suspend operator fun invoke(): RecipeSummary = repository.getRecipeSummary()
}