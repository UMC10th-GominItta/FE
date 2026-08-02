package com.gominitta.android.presentation.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RecipeViewModel @JvmOverloads constructor(
    private val repository: RecipeRepository = DummyRecipeRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(RecipeUiState())
        private set

    init {
        uiState = uiState.copy(recipes = repository.getRecipes())
    }

    fun selectRecipe(recipeId: Long) {
        uiState = uiState.copy(
            selectedRecipeId = recipeId,
            runStatus = RecipeRunStatus.Ready,
        )
    }

    fun createRecipe(title: String, description: String, durationMinutes: Int) {
        val newRecipe = RecipeItem(
            id = nextRecipeId(),
            title = title,
            description = description,
            durationMinutes = durationMinutes,
        )
        repository.addRecipe(newRecipe)
        uiState = uiState.copy(
            recipes = repository.getRecipes(),
            createTitle = "",
            createDescription = "",
            createDuration = "",
        )
    }

    fun updateRecipe(recipeId: Long, title: String, description: String, durationMinutes: Int) {
        val updated = RecipeItem(recipeId, title, description, durationMinutes)
        repository.updateRecipe(updated)
        uiState = uiState.copy(recipes = repository.getRecipes())
    }

    fun deleteRecipe(recipeId: Long) {
        repository.deleteRecipe(recipeId)
        uiState = uiState.copy(
            recipes = repository.getRecipes(),
            selectedRecipeId = if (uiState.selectedRecipeId == recipeId) null else uiState.selectedRecipeId,
        )
    }

    fun startRecipe() {
        uiState = uiState.copy(runStatus = RecipeRunStatus.Running)
    }

    fun completeRecipe() {
        uiState = uiState.copy(
            runStatus = RecipeRunStatus.Completed,
            completionSummary = uiState.completionSummary.copy( // 추가 — 완료 통계 누적
                todayCompletedCount = uiState.completionSummary.todayCompletedCount + 1,
                totalCompletedCount = uiState.completionSummary.totalCompletedCount + 1,
            ),
        )
    }

    private fun nextRecipeId(): Long {
        return (uiState.recipes.maxOfOrNull { it.id } ?: 0L) + 1L
    }
}