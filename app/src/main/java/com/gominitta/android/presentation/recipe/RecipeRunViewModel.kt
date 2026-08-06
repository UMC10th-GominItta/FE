package com.gominitta.android.presentation.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.CompleteRecipeUseCase
import com.gominitta.android.domain.usecase.GetRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * D102 레시피 실행 화면 전용 ViewModel.
 * recipeId는 route("reciperun/{recipeId}")에서 SavedStateHandle로 받는다.
 */
@HiltViewModel
class RecipeRunViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val completeRecipeUseCase: CompleteRecipeUseCase,
) : ViewModel() {

    private val recipeId: Long = checkNotNull(savedStateHandle["recipeId"])

    var recipe by mutableStateOf<RecipeItem?>(null)
        private set

    var runStatus by mutableStateOf(RecipeRunStatus.Ready)
        private set

    var isFinished by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            recipe = getRecipeUseCase(recipeId).toUiItem()
        }
    }

    fun startRun() {
        runStatus = RecipeRunStatus.Running
    }

    fun onTimerFinished() {
        runStatus = RecipeRunStatus.Completed
    }

    fun onFinishClick() {
        viewModelScope.launch {
            completeRecipeUseCase(recipeId)
            isFinished = true
        }
    }
}