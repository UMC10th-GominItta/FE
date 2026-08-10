package com.gominitta.android.presentation.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.CompleteRecipeLogUseCase
import com.gominitta.android.domain.usecase.GetRecipeUseCase
import com.gominitta.android.domain.usecase.StartRecipeLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeRunViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val startRecipeLogUseCase: StartRecipeLogUseCase,
    private val completeRecipeLogUseCase: CompleteRecipeLogUseCase,
) : ViewModel() {

    private val recipeId: Long = checkNotNull(savedStateHandle["recipeId"])
    private var recipeLogId: Long? = null

    var recipe by mutableStateOf<RecipeItem?>(null)
        private set

    var runStatus by mutableStateOf(RecipeRunStatus.Ready)
        private set

    var isFinished by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            recipe = getRecipeUseCase(recipeId).toUiItem()
            recipeLogId = startRecipeLogUseCase(recipeId)
        }
    }

    fun startRun() {
        runStatus = RecipeRunStatus.Running
    }

    fun onTimerFinished() {
        runStatus = RecipeRunStatus.Completed
    }

    fun onFinishClick() {
        val logId = recipeLogId ?: return
        viewModelScope.launch {
            completeRecipeLogUseCase(logId)
            isFinished = true
        }
    }
    fun pauseRun() {
        runStatus = RecipeRunStatus.Paused
    }

    fun resumeRun() {
        runStatus = RecipeRunStatus.Running
    }
}