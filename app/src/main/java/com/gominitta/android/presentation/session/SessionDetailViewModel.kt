package com.gominitta.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.UpdateRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionDetailUiState(
    val recordText: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isDone: Boolean = false,
)

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val updateRecord: UpdateRecordUseCase,
    private val flowState: SessionFlowState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SessionDetailUiState(recordText = flowState.recordText.orEmpty()),
    )
    val uiState: StateFlow<SessionDetailUiState> = _uiState.asStateFlow()

    fun updateRecordText(text: String) {
        _uiState.update { it.copy(recordText = text) }
    }

    /** "저장하기" 클릭. 기록이 없으면(음성/필기 탭이었거나 빈 텍스트) 서버 호출 없이 바로 넘어간다. */
    fun save() {
        val sessionId = flowState.sessionId
        val recordId = flowState.recordId
        if (sessionId == null || recordId == null) {
            _uiState.update { it.copy(isDone = true) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                updateRecord(sessionId, recordId, _uiState.value.recordText)
                _uiState.update { it.copy(isSaving = false, isDone = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }
}
