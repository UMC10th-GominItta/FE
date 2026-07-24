package com.gominitta.android.presentation.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RecipeViewModel @JvmOverloads constructor( // 추가 — viewModel() 리플렉션 생성 대응
    private val repository: RecipeRepository = DummyRecipeRepository(), // 추가
) : ViewModel() {

    var uiState by mutableStateOf(RecipeUiState())
        private set

    init { // 추가 — 초기 진입 시 더미 레시피 목록 로드
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
        repository.addRecipe(newRecipe) // 추가
        uiState = uiState.copy(
            recipes = repository.getRecipes(), // 변경
            createTitle = "",
            createDescription = "",
            createDuration = "",
        )
    }

    fun updateRecipe(recipeId: Long, title: String, description: String, durationMinutes: Int) {
        val updated = RecipeItem(recipeId, title, description, durationMinutes)
        repository.updateRecipe(updated) // 추가
        uiState = uiState.copy(recipes = repository.getRecipes()) // 변경
    }

    fun deleteRecipe(recipeId: Long) {
        repository.deleteRecipe(recipeId) // 추가
        uiState = uiState.copy(
            recipes = repository.getRecipes(), // 변경
            selectedRecipeId = if (uiState.selectedRecipeId == recipeId) null else uiState.selectedRecipeId,
        )
    }

    fun startRecipe() {
        uiState = uiState.copy(runStatus = RecipeRunStatus.Running)
    }

    fun completeRecipe() {
        uiState = uiState.copy(runStatus = RecipeRunStatus.Completed)
    }

    private fun nextRecipeId(): Long {
        return (uiState.recipes.maxOfOrNull { it.id } ?: 0L) + 1L
    }
}