package com.gominitta.android.presentation.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.CreateRecipeUseCase
import com.gominitta.android.domain.usecase.DeleteRecipeUseCase
import com.gominitta.android.domain.usecase.GetRecipeSummaryUseCase
import com.gominitta.android.domain.usecase.GetRecipesUseCase
import com.gominitta.android.domain.usecase.UpdateRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val createRecipeUseCase: CreateRecipeUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val getRecipeSummaryUseCase: GetRecipeSummaryUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(RecipeUiState())
        private set

    init {
        viewModelScope.launch {
            uiState = uiState.copy(
                recipes = getRecipesUseCase().map { it.toUiItem() },
                completionSummary = getRecipeSummaryUseCase().toUi(),
            )
        }
    }

    fun selectRecipe(recipeId: Long) {
        uiState = uiState.copy(
            selectedRecipeId = recipeId,
            runStatus = RecipeRunStatus.Ready,
        )
    }

    fun createRecipe(title: String, description: String, durationMinutes: Int) {
        viewModelScope.launch {
            val newRecipe = RecipeItem(
                id = nextRecipeId(),
                title = title,
                description = description,
                durationMinutes = durationMinutes,
            )
            createRecipeUseCase(newRecipe.toDomain())
            uiState = uiState.copy(
                recipes = getRecipesUseCase().map { it.toUiItem() },
                createTitle = "",
                createDescription = "",
                createDuration = "",
            )
        }
    }

    fun updateRecipe(recipeId: Long, title: String, description: String, durationMinutes: Int) {
        viewModelScope.launch {
            val updated = RecipeItem(recipeId, title, description, durationMinutes)
            updateRecipeUseCase(updated.toDomain())
            uiState = uiState.copy(recipes = getRecipesUseCase().map { it.toUiItem() })
        }
    }

    fun deleteRecipe(recipeId: Long) {
        viewModelScope.launch {
            deleteRecipeUseCase(recipeId)
            uiState = uiState.copy(
                recipes = getRecipesUseCase().map { it.toUiItem() },
                selectedRecipeId = if (uiState.selectedRecipeId == recipeId) null else uiState.selectedRecipeId,
            )
        }
    }

    fun startRecipe() {
        uiState = uiState.copy(runStatus = RecipeRunStatus.Running)
    }

    fun completeRecipe() {
        // TODO: 완료 API(레시피_완료.md, PATCH /api/v1/recipe-logs/{id}) 연동 시
        //       CompleteRecipeUseCase로 교체 예정. 지금은 로컬 통계만 누적.
        uiState = uiState.copy(
            runStatus = RecipeRunStatus.Completed,
            completionSummary = uiState.completionSummary.copy(
                todayCompletedCount = uiState.completionSummary.todayCompletedCount + 1,
                totalCompletedCount = uiState.completionSummary.totalCompletedCount + 1,
            ),
        )
    }

    private fun nextRecipeId(): Long {
        return (uiState.recipes.maxOfOrNull { it.id } ?: 0L) + 1L
    }
}