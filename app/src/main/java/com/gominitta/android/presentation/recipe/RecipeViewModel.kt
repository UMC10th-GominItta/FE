package com.gominitta.android.presentation.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RecipeViewModel : ViewModel() {
    var uiState by mutableStateOf(RecipeUiState())
        private set

    fun selectRecipe(recipeId: Long) {
        uiState = uiState.copy(
            selectedRecipeId = recipeId,
            runStatus = RecipeRunStatus.Ready,
        )
    }

    fun createRecipe(
        title: String,
        description: String,
        durationMinutes: Int,
    ) {
        val newRecipe = RecipeItem(
            id = nextRecipeId(),
            title = title,
            description = description,
            durationMinutes = durationMinutes,
        )

        uiState = uiState.copy(
            recipes = uiState.recipes + newRecipe,
            createTitle = "",
            createDescription = "",
            createDuration = "",
        )
    }

    fun updateRecipe(
        recipeId: Long,
        title: String,
        description: String,
        durationMinutes: Int,
    ) {
        uiState = uiState.copy(
            recipes = uiState.recipes.map { recipe ->
                if (recipe.id == recipeId) {
                    recipe.copy(
                        title = title,
                        description = description,
                        durationMinutes = durationMinutes,
                    )
                } else {
                    recipe
                }
            },
        )
    }

    fun deleteRecipe(recipeId: Long) {
        uiState = uiState.copy(
            recipes = uiState.recipes.filterNot { recipe ->
                recipe.id == recipeId
            },
            selectedRecipeId = if (uiState.selectedRecipeId == recipeId) {
                null
            } else {
                uiState.selectedRecipeId
            },
        )
    }

    fun startRecipe() {
        uiState = uiState.copy(
            runStatus = RecipeRunStatus.Running,
        )
    }

    fun completeRecipe() {
        uiState = uiState.copy(
            runStatus = RecipeRunStatus.Completed,
        )
    }

    private fun nextRecipeId(): Long {
        return (uiState.recipes.maxOfOrNull { recipe -> recipe.id } ?: 0L) + 1L
    }
}