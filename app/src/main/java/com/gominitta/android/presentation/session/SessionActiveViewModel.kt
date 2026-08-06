package com.gominitta.android.presentation.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.R
import com.gominitta.android.domain.usecase.AddTextRecordUseCase
import com.gominitta.android.domain.usecase.GetSessionDetailUseCase
import com.gominitta.android.domain.usecase.StartSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** UI 탭 — raw 값이 서버 RecordType(text/voice/handwriting)과 맞아떨어진다. */
enum class RecordTab(val raw: String, val label: String, val icon: Int) {
    Text("text", "텍스트 입력", R.drawable.ic_textpencil),
    Voice("voice", "음성 인식", R.drawable.ic_mic),
    Handwriting("handwriting", "텍스트 인식", R.drawable.ic_camera),
}

data class SessionActiveUiState(
    val isLoading: Boolean = true,
    val worryContent: String = "",
    val themeCategory: String = "",
    val selectedTab: RecordTab = RecordTab.Text,
    val noteText: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isDone: Boolean = false,
)

@HiltViewModel
class SessionActiveViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSessionDetail: GetSessionDetailUseCase,
    private val startSession: StartSessionUseCase,
    private val addTextRecord: AddTextRecordUseCase,
    private val flowState: SessionFlowState,
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    private val _uiState = MutableStateFlow(SessionActiveUiState())
    val uiState: StateFlow<SessionActiveUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                startSession(sessionId)
                val session = getSessionDetail(sessionId)
                flowState.start(sessionId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        worryContent = session.worryContent,
                        themeCategory = session.themeCategory.orEmpty(),
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun selectTab(tab: RecordTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun updateNoteText(text: String) {
        _uiState.update { it.copy(noteText = text) }
    }

    /**
     * "세션 완료하기" 클릭. 텍스트 탭에 내용이 있으면 그 자리에서 기록을 실제로 생성하고
     * (음성/필기 탭은 아직 실촬영·녹음이 없어서 건너뜀), 다음 화면으로 넘어간다.
     */
    fun commitAndProceed() {
        val state = _uiState.value
        val text = state.noteText.trim()
        if (state.selectedTab != RecordTab.Text || text.isEmpty()) {
            _uiState.update { it.copy(isDone = true) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val record = addTextRecord(sessionId, text)
                flowState.setRecord(record.id, record.contentText)
                _uiState.update { it.copy(isSaving = false, isDone = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }
}
