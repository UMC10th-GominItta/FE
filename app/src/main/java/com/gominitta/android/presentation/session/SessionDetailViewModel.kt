package com.gominitta.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.AddTextRecordUseCase
import com.gominitta.android.domain.usecase.DeleteRecordUseCase
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
    private val addTextRecord: AddTextRecordUseCase,
    private val deleteRecord: DeleteRecordUseCase,
    private val flowState: SessionFlowState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SessionDetailUiState(recordText = flowState.recordText.orEmpty()),
    )
    val uiState: StateFlow<SessionDetailUiState> = _uiState.asStateFlow()

    fun updateRecordText(text: String) {
        _uiState.update { it.copy(recordText = text) }
    }

    /**
     * [SessionDetailUiState.isDone] 소비 완료 신호. onSave()로 다음 화면(SESSION_COMPLETE)에
     * 이동한 뒤에도 이 화면의 백스택 엔트리·ViewModel은 살아있으므로, 리셋하지 않으면
     * 뒤로가기로 돌아오자마자 LaunchedEffect가 다시 true를 보고 즉시 재이동해 버린다.
     */
    fun onDoneHandled() {
        _uiState.update { it.copy(isDone = false) }
    }

    /** "저장하기" 클릭. 기록이 없으면 생성, 내용을 다 지웠으면 삭제(서버가 빈 내용을 거부함), 그 외엔 수정. */
    fun save() {
        val sessionId = flowState.sessionId
        val recordId = flowState.recordId
        val text = _uiState.value.recordText
        if (sessionId == null || (recordId == null && text.isBlank())) {
            _uiState.update { it.copy(isDone = true) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                // flowState도 같이 갱신해 재저장 시 중복 생성·재삭제를 막는다.
                when {
                    recordId == null -> {
                        val record = addTextRecord(sessionId, text)
                        flowState.setRecord(record.id, record.contentText)
                    }
                    text.isBlank() -> {
                        deleteRecord(sessionId, recordId)
                        flowState.clearRecord()
                    }
                    else -> {
                        val record = updateRecord(sessionId, recordId, text)
                        flowState.setRecord(record.id, record.contentText)
                    }
                }
                _uiState.update { it.copy(isSaving = false, isDone = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }
}
