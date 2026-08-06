package com.gominitta.android.presentation.recipe

import com.gominitta.android.domain.model.recipe.Recipe
import com.gominitta.android.domain.model.recipe.RecipeSummary

/**
 * domain Recipe ↔ presentation RecipeItem 매핑.
 *
 * durationMinutes(Int, UI) ↔ estimatedMinutes(Long, domain) 타입이 다르므로
 * 여기서만 변환하고, 화면/ViewModel 다른 곳에서는 신경 쓰지 않아도 되게 한다.
 */
fun Recipe.toUiItem(): RecipeItem = RecipeItem(
    id = id,
    title = title,
    description = description,
    durationMinutes = estimatedMinutes.toInt(),
)

fun RecipeItem.toDomain(): Recipe = Recipe(
    id = id,
    title = title,
    description = description,
    estimatedMinutes = durationMinutes.toLong(),
)

fun RecipeSummary.toUi(): RecipeCompletionSummary = RecipeCompletionSummary(
    todayCompletedCount = todayCompletedCount.toInt(),
    totalCompletedCount = totalCompletedCount.toInt(),
)