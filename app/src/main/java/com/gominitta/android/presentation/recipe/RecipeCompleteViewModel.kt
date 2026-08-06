package com.gominitta.android.presentation.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.GetRecipeSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * D102-2 레시피 완료 통계 화면 전용 ViewModel.
 */
@HiltViewModel
class RecipeCompleteViewModel @Inject constructor(
    private val getRecipeSummaryUseCase: GetRecipeSummaryUseCase,
) : ViewModel() {

    var summary by mutableStateOf(RecipeCompletionSummary())
        private set

    init {
        viewModelScope.launch {
            summary = getRecipeSummaryUseCase().toUi()
        }
    }
}