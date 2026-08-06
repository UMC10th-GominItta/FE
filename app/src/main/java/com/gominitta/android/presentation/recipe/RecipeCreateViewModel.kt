package com.gominitta.android.presentation.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.CreateRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_DURATION_MINUTES = 1
private const val MAX_DURATION_MINUTES = 60

/**
 * D104 새 레시피 등록 화면 전용 ViewModel.
 */
@HiltViewModel
class RecipeCreateViewModel @Inject constructor(
    private val createRecipeUseCase: CreateRecipeUseCase,
) : ViewModel() {

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var duration by mutableStateOf("")
        private set

    var durationErrorMessage by mutableStateOf<String?>(null)
        private set

    var selectedRecommendedTitle by mutableStateOf<String?>(null)
        private set

    var isRegistered by mutableStateOf(false)
        private set

    val isRegisterEnabled: Boolean
        get() = title.isNotBlank() && description.isNotBlank()

    fun onTitleChange(newTitle: String) {
        title = newTitle
        selectedRecommendedTitle = null
    }

    fun onDescriptionChange(newDescription: String) {
        description = newDescription
        selectedRecommendedTitle = null
    }

    fun onDurationChange(newDuration: String) {
        duration = newDuration.filter(Char::isDigit)
        selectedRecommendedTitle = null
        durationErrorMessage = null
    }

    fun onRecommendedRecipeSelected(recommendedRecipe: RecommendedRecipe) {
        selectedRecommendedTitle = recommendedRecipe.title
        title = recommendedRecipe.title
        description = recommendedRecipe.description
        duration = recommendedRecipe.durationMinutes.toString()
        durationErrorMessage = null
    }

    fun onRegisterClick() {
        val minutes = validateDurationOrShowError() ?: return

        viewModelScope.launch {
            createRecipeUseCase(
                RecipeItem(
                    id = 0L, // 서버/Repository에서 실제 id 발급 — 등록 시점엔 임시값
                    title = title.trim(),
                    description = description.trim(),
                    durationMinutes = minutes,
                ).toDomain(),
            )
            isRegistered = true
        }
    }

    private fun validateDurationOrShowError(): Int? {
        val minutes = duration.toIntOrNull()
        return when {
            duration.isBlank() -> {
                durationErrorMessage = "예상 소요 시간을 입력해주세요"
                null
            }
            minutes == null || minutes !in MIN_DURATION_MINUTES..MAX_DURATION_MINUTES -> {
                durationErrorMessage =
                    "${MIN_DURATION_MINUTES}~${MAX_DURATION_MINUTES}분 사이의 시간을 입력해주세요"
                null
            }
            else -> {
                durationErrorMessage = null
                minutes
            }
        }
    }
}