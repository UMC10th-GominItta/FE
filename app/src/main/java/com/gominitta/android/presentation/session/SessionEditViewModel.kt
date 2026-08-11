package com.gominitta.android.presentation.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.usecase.DeleteWorryUseCase
import com.gominitta.android.domain.usecase.GetWorryUseCase
import com.gominitta.android.domain.usecase.UpdateWorryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionEditUiState(
    val isLoading: Boolean = true,
    val loadErrorMessage: String? = null,
    val title: String = "",
    val content: String = "",
    val startAt: LocalDateTime? = null,
    val endAt: LocalDateTime? = null,
    val isDirty: Boolean = false,
    val isSaving: Boolean = false,
    val saveErrorMessage: String? = null,
    val isDone: Boolean = false,
    val isDeleted: Boolean = false,
)

/** 예약된 걱정 수정(C105) — 세션 목록의 "수정/삭제"에서 진입, 대상은 세션이 아니라 그 세션을 만든 걱정(worry)이다. */
@HiltViewModel
class SessionEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getWorry: GetWorryUseCase,
    private val updateWorry: UpdateWorryUseCase,
    private val deleteWorry: DeleteWorryUseCase,
) : ViewModel() {

    private val worryId: Long = checkNotNull(savedStateHandle["worryId"])

    private val _uiState = MutableStateFlow(SessionEditUiState())
    val uiState: StateFlow<SessionEditUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadErrorMessage = null) }
            when (val result = getWorry(worryId)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        title = result.data.title,
                        content = result.data.content,
                        startAt = result.data.scheduledStartAt,
                        endAt = result.data.scheduledEndAt,
                    )
                }
                is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, loadErrorMessage = result.message) }
                is ApiResult.NetworkError ->
                    _uiState.update { it.copy(isLoading = false, loadErrorMessage = "네트워크 연결을 확인해 주세요.") }
            }
        }
    }

    fun updateTitle(text: String) {
        _uiState.update { it.copy(title = text, isDirty = true) }
    }

    fun updateContent(text: String) {
        _uiState.update { it.copy(content = text, isDirty = true) }
    }

    fun updateStartAt(dateTime: LocalDateTime) {
        _uiState.update { it.copy(startAt = dateTime, isDirty = true) }
    }

    fun updateEndAt(dateTime: LocalDateTime) {
        _uiState.update { it.copy(endAt = dateTime, isDirty = true) }
    }

    fun save() {
        val state = _uiState.value
        val start = state.startAt ?: return
        val end = state.endAt ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveErrorMessage = null) }
            when (
                val result = updateWorry(
                    worryId = worryId,
                    title = state.title,
                    content = state.content,
                    scheduledStartAt = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    scheduledEndAt = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                )
            ) {
                is ApiResult.Success -> _uiState.update { it.copy(isSaving = false, isDone = true) }
                is ApiResult.Error -> _uiState.update { it.copy(isSaving = false, saveErrorMessage = result.message) }
                is ApiResult.NetworkError ->
                    _uiState.update { it.copy(isSaving = false, saveErrorMessage = "네트워크 연결을 확인해 주세요.") }
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveErrorMessage = null) }
            when (val result = deleteWorry(worryId)) {
                is ApiResult.Success -> _uiState.update { it.copy(isSaving = false, isDeleted = true) }
                is ApiResult.Error -> _uiState.update { it.copy(isSaving = false, saveErrorMessage = result.message) }
                is ApiResult.NetworkError ->
                    _uiState.update { it.copy(isSaving = false, saveErrorMessage = "네트워크 연결을 확인해 주세요.") }
            }
        }
    }
}
