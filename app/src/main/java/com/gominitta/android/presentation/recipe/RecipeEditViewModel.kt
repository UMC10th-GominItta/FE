package com.gominitta.android.presentation.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.DeleteRecipeUseCase
import com.gominitta.android.domain.usecase.GetRecipeUseCase
import com.gominitta.android.domain.usecase.UpdateRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_DURATION_MINUTES = 1
private const val MAX_DURATION_MINUTES = 60

/**
 * D103 레시피 수정 화면 전용 ViewModel.
 *
 * recipeId는 화면 파라미터가 아니라 navigation route("recipeedit/{recipeId}")에서
 * SavedStateHandle을 통해 직접 꺼낸다. 화면 회전/프로세스 재생성에도 유지됨.
 */
@HiltViewModel
class RecipeEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
) : ViewModel() {

    private val recipeId: Long = checkNotNull(savedStateHandle["recipeId"])

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var duration by mutableStateOf("")
        private set

    var durationErrorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var isDeleted by mutableStateOf(false)
        private set

    var isUpdated by mutableStateOf(false)
        private set

    val isCompleteEnabled: Boolean
        get() = title.isNotBlank() && description.isNotBlank()

    init {
        viewModelScope.launch {
            val recipe = getRecipeUseCase(recipeId)
            title = recipe.title
            description = recipe.description
            duration = recipe.estimatedMinutes.toString()
            isLoading = false
        }
    }

    fun onTitleChange(newTitle: String) {
        title = newTitle
    }

    fun onDescriptionChange(newDescription: String) {
        description = newDescription
    }

    fun onDurationChange(newDuration: String) {
        duration = newDuration.filter(Char::isDigit)
        durationErrorMessage = null
    }

    fun onCompleteClick() {
        val minutes = validateDurationOrShowError() ?: return

        viewModelScope.launch {
            updateRecipeUseCase(
                RecipeItem(
                    id = recipeId,
                    title = title.trim(),
                    description = description.trim(),
                    durationMinutes = minutes,
                ).toDomain(),
            )
            isUpdated = true
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            deleteRecipeUseCase(recipeId)
            isDeleted = true
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