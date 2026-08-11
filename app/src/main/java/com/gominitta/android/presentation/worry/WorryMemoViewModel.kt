package com.gominitta.android.presentation.worry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.usecase.AddWorryContentUseCase
import com.gominitta.android.domain.usecase.GetSessionDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorryMemoUiState(
    val isSaving: Boolean = false,
    val isDone: Boolean = false,
    val errorMessage: String? = null,
)

/** 한 줄 보태기(B104) — sessionId로 진입해 대상 걱정(worry)을 역참조한 뒤 내용을 덧붙인다. */
@HiltViewModel
class WorryMemoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSessionDetail: GetSessionDetailUseCase,
    private val addWorryContent: AddWorryContentUseCase,
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    private val _uiState = MutableStateFlow(WorryMemoUiState())
    val uiState: StateFlow<WorryMemoUiState> = _uiState.asStateFlow()

    fun save(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val worryId = runCatching { getSessionDetail(sessionId).worryId }.getOrNull()
            if (worryId == null) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = "걱정 정보를 불러오지 못했어요. 다시 시도해 주세요.")
                }
                return@launch
            }
            when (val result = addWorryContent(worryId, content)) {
                is ApiResult.Success -> _uiState.update { it.copy(isSaving = false, isDone = true) }
                is ApiResult.Error -> _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                is ApiResult.NetworkError ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = "네트워크 연결을 확인해 주세요.") }
            }
        }
    }
}
