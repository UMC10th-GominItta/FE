package com.gominitta.android.presentation.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.R
import com.gominitta.android.domain.usecase.AddHandwritingRecordUseCase
import com.gominitta.android.domain.usecase.AddTextRecordUseCase
import com.gominitta.android.domain.usecase.AddVoiceRecordUseCase
import com.gominitta.android.domain.usecase.GetSessionDetailUseCase
import com.gominitta.android.domain.usecase.StartSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
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
    /** 화면 진입 시 세션 로딩 실패 — 화면 전체를 에러로 대체한다. */
    val loadErrorMessage: String? = null,
    /** 기록 저장/업로드 실패 — 화면은 그대로 두고 인라인으로만 보여준다. */
    val recordErrorMessage: String? = null,
    val isDone: Boolean = false,
    /** 음성/필기 탭에서 이미 업로드까지 끝난 기록의 탭. 잠깐 뱃지로 보여준 뒤 자동으로 null로 돌아간다. */
    val capturedTab: RecordTab? = null,
)

@HiltViewModel
class SessionActiveViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSessionDetail: GetSessionDetailUseCase,
    private val startSession: StartSessionUseCase,
    private val addTextRecord: AddTextRecordUseCase,
    private val addVoiceRecord: AddVoiceRecordUseCase,
    private val addHandwritingRecord: AddHandwritingRecordUseCase,
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
            _uiState.update { it.copy(isLoading = true, loadErrorMessage = null) }
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
                _uiState.update { it.copy(isLoading = false, loadErrorMessage = e.message) }
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
     * "세션 완료하기" 클릭. 텍스트 탭에 내용이 있으면 그 자리에서 기록을 실제로 생성한다.
     * 음성/필기 탭은 녹음·촬영 시점에 이미 업로드가 끝나 있으므로([capturedTab]) 그대로 진행.
     */
    fun commitAndProceed() {
        val state = _uiState.value
        if (state.capturedTab != null) {
            _uiState.update { it.copy(isDone = true) }
            return
        }
        val text = state.noteText.trim()
        if (state.selectedTab != RecordTab.Text || text.isEmpty()) {
            _uiState.update { it.copy(isDone = true) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, recordErrorMessage = null) }
            try {
                val record = addTextRecord(sessionId, text)
                flowState.setRecord(record.id, record.contentText)
                _uiState.update { it.copy(isSaving = false, isDone = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, recordErrorMessage = e.message) }
            }
        }
    }

    /** 음성 탭에서 녹음이 끝나 파일이 생기면 곧바로 업로드해 STT 결과를 기록으로 저장한다. */
    fun uploadVoiceRecord(file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, recordErrorMessage = null) }
            try {
                val record = addVoiceRecord(sessionId, file)
                flowState.setRecord(record.id, record.contentText)
                _uiState.update { it.copy(isSaving = false, capturedTab = RecordTab.Voice) }
                clearCapturedTabAfterDelay()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, recordErrorMessage = e.message) }
            } finally {
                file.delete()
            }
        }
    }

    /** 필기 탭에서 촬영이 끝나 파일이 생기면 곧바로 업로드해 OCR 결과를 기록으로 저장한다. */
    fun uploadHandwritingRecord(file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, recordErrorMessage = null) }
            try {
                val record = addHandwritingRecord(sessionId, file)
                flowState.setRecord(record.id, record.contentText)
                _uiState.update { it.copy(isSaving = false, capturedTab = RecordTab.Handwriting) }
                clearCapturedTabAfterDelay()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, recordErrorMessage = e.message) }
            } finally {
                file.delete()
            }
        }
    }

    /** "저장됐어요" 뱃지를 잠깐만 보여주고 자동으로 치운다. */
    private fun clearCapturedTabAfterDelay() {
        viewModelScope.launch {
            delay(CAPTURED_BADGE_DURATION_MS)
            _uiState.update { it.copy(capturedTab = null) }
        }
    }

    private companion object {
        const val CAPTURED_BADGE_DURATION_MS = 1800L
    }
}
