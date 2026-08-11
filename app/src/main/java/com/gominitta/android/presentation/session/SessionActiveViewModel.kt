package com.gominitta.android.presentation.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.R
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.usecase.AddHandwritingRecordUseCase
import com.gominitta.android.domain.usecase.AddTextRecordUseCase
import com.gominitta.android.domain.usecase.AddVoiceRecordUseCase
import com.gominitta.android.domain.usecase.GetSessionDetailUseCase
import com.gominitta.android.domain.usecase.GetWorryUseCase
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
    val worryTitle: String = "",
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
    private val getWorry: GetWorryUseCase,
    private val startSession: StartSessionUseCase,
    private val addTextRecord: AddTextRecordUseCase,
    private val addVoiceRecord: AddVoiceRecordUseCase,
    private val addHandwritingRecord: AddHandwritingRecordUseCase,
    private val flowState: SessionFlowState,
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    /**
     * 화면 진입 시점엔 세션 상태를 SCHEDULED/INCOMPLETE 그대로 둔다(=IN_PROGRESS로 전이 안 함).
     * "세션 완료하기"를 눌러야 비로소 [commitAndProceed]에서 한 번 시작 처리한다 — 그래야
     * 기록만 남기고 완료 안 한 채 나가도 세션이 예정/미완료 목록에 계속 보인다(진행 중 상태는
     * 목록 조회 API가 지원을 안 해서 그 상태가 되는 순간 목록에서 사라짐).
     */
    private var isStarted = false

    private val _uiState = MutableStateFlow(SessionActiveUiState())
    val uiState: StateFlow<SessionActiveUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadErrorMessage = null) }
            try {
                val session = getSessionDetail(sessionId)
                isStarted = session.status == SessionStatus.IN_PROGRESS
                flowState.start(sessionId)
                // worryTitle/worryContent는 세션 생성 시점 스냅샷이라 걱정 수정이 반영 안 된다(백엔드 한계).
                // 최신 걱정 내용으로 덮어쓰고, 조회 실패 시엔 스냅샷을 그대로 보여준다.
                val liveWorry = when (val result = getWorry(session.worryId)) {
                    is ApiResult.Success -> result.data
                    is ApiResult.Error, is ApiResult.NetworkError -> null
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        worryTitle = liveWorry?.title ?: session.worryTitle,
                        worryContent = liveWorry?.content ?: session.worryContent,
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

    /**
     * [SessionActiveUiState.isDone] 소비 완료 신호. 이 화면은 네비게이션 뒤로가기로
     * 다시 돌아올 수 있는 스택 최하단 화면이므로, isDone을 리셋하지 않으면 복귀 즉시
     * LaunchedEffect가 다시 true를 보고 onNavigateNext()를 재실행해 뒤로가기가 막힌다.
     */
    fun onDoneHandled() {
        _uiState.update { it.copy(isDone = false) }
    }

    fun selectTab(tab: RecordTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun updateNoteText(text: String) {
        _uiState.update { it.copy(noteText = text) }
    }

    /**
     * "세션 완료하기" 클릭. 음성/필기 탭은 녹음·촬영 시점에 이미 업로드가 끝나 있으므로([capturedTab])
     * 손댈 게 없지만, 텍스트 탭의 [SessionActiveUiState.noteText]는 현재 선택된 탭이나 [capturedTab]
     * 여부와 무관하게 내용이 있으면 항상 저장한다 — 그렇지 않으면 음성/사진을 같이 기록했을 때
     * 텍스트만 조용히 유실된다.
     */
    fun commitAndProceed() {
        val state = _uiState.value
        val text = state.noteText.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, recordErrorMessage = null) }
            try {
                // 평가 저장(completeSession)은 서버가 IN_PROGRESS 상태만 완료 처리를 허용해서,
                // 여태 미뤄둔 시작 처리를 여기서 딱 한 번 한다.
                if (!isStarted) {
                    startSession(sessionId)
                    isStarted = true
                }
                if (text.isNotEmpty()) {
                    val record = addTextRecord(sessionId, text)
                    flowState.setRecord(record.id, record.contentText)
                }
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
