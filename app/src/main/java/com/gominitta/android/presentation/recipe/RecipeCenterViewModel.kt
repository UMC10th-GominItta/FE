package com.gominitta.android.presentation.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.DeleteRecipeUseCase
import com.gominitta.android.domain.usecase.GetRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * D101 마음 레시피 센터(목록) 화면 전용 ViewModel.
 */
@HiltViewModel
class RecipeCenterViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
) : ViewModel() {

    var recipes by mutableStateOf<List<RecipeItem>>(emptyList())
        private set

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            recipes = getRecipesUseCase().map { it.toUiItem() }
        }
    }

    fun deleteRecipe(recipeId: Long) {
        viewModelScope.launch {
            deleteRecipeUseCase(recipeId)
            recipes = getRecipesUseCase().map { it.toUiItem() }
        }
    }
    fun refresh() {
        loadRecipes()
    }
}