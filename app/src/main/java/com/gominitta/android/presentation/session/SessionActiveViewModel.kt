package com.gominitta.android.presentation.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.R
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.session.RecordType
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.usecase.AddHandwritingRecordUseCase
import com.gominitta.android.domain.usecase.AddTextRecordUseCase
import com.gominitta.android.domain.usecase.AddVoiceRecordUseCase
import com.gominitta.android.domain.usecase.DeleteRecordUseCase
import com.gominitta.android.domain.usecase.GetSessionDetailUseCase
import com.gominitta.android.domain.usecase.GetWorryUseCase
import com.gominitta.android.domain.usecase.UpdateRecordUseCase
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
    /** 뒤로가기로 적어둔 내용까지 저장을 끝낸 상태. 화면을 벗어나야 한다는 신호. */
    val isExited: Boolean = false,
    /** 음성/필기 탭에서 이미 업로드까지 끝난 기록의 탭. 잠깐 뱃지로 보여준 뒤 자동으로 null로 돌아간다. */
    val capturedTab: RecordTab? = null,
)

@HiltViewModel
class SessionActiveViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSessionDetail: GetSessionDetailUseCase,
    private val getWorry: GetWorryUseCase,
    private val addTextRecord: AddTextRecordUseCase,
    private val updateRecord: UpdateRecordUseCase,
    private val deleteRecord: DeleteRecordUseCase,
    private val addVoiceRecord: AddVoiceRecordUseCase,
    private val addHandwritingRecord: AddHandwritingRecordUseCase,
    private val flowState: SessionFlowState,
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    /** 이미 저장해둔 텍스트 기록. 재진입·재저장 때 새로 만들지 않고 이걸 고친다. */
    private var textRecordId: Long? = null

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
                flowState.start(sessionId, session.status == SessionStatus.IN_PROGRESS)
                // worryTitle/worryContent는 세션 생성 시점 스냅샷이라 걱정 수정이 반영 안 된다(백엔드 한계).
                // 최신 걱정 내용으로 덮어쓰고, 조회 실패 시엔 스냅샷을 그대로 보여준다.
                val liveWorry = when (val result = getWorry(session.worryId)) {
                    is ApiResult.Success -> result.data
                    is ApiResult.Error, is ApiResult.NetworkError -> null
                }
                // 지난번에 적어둔 텍스트를 그대로 이어서 쓰게 되살린다.
                val savedNote = session.records.lastOrNull { it.recordType == RecordType.TEXT }
                textRecordId = savedNote?.id
                savedNote?.let { flowState.setRecord(it.id, it.contentText) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        worryTitle = liveWorry?.title ?: session.worryTitle,
                        worryContent = liveWorry?.content ?: session.worryContent,
                        themeCategory = session.themeCategory.orEmpty(),
                        noteText = savedNote?.contentText.orEmpty(),
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

    /**
     * 기록 확인 화면([SessionDetailScreen])에서 고친 내용을 되돌아왔을 때 텍스트 칸에 반영한다.
     *
     * 확인 화면이 편집한 게 이 화면의 텍스트 기록일 때만 덮어쓴다 — 텍스트를 비운 채 음성·필기만
     * 남긴 경우 [flowState]가 들고 있는 건 STT/OCR 기록이라, 그대로 가져오면 그 내용이 텍스트
     * 칸에 들어가 완료 시 같은 내용이 텍스트 기록으로 한 번 더 저장된다.
     */
    fun syncEditedNote() {
        val editedId = flowState.recordId
        when {
            editedId != null && editedId == textRecordId ->
                _uiState.update { it.copy(noteText = flowState.recordText.orEmpty()) }
            // 확인 화면에서 내용을 다 지워 텍스트 기록이 삭제된 경우.
            editedId == null && textRecordId != null -> {
                textRecordId = null
                _uiState.update { it.copy(noteText = "") }
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
     * "세션 완료하기" 클릭. 음성/필기는 이미 업로드가 끝나 있고, 텍스트만 여기서 저장한다
     * (선택된 탭과 무관하게 — 안 그러면 음성/사진과 같이 적은 텍스트가 유실된다).
     *
     * 세션 시작(in_progress) 처리는 여기서 하지 않는다. 평가 저장 직전까지 미뤄야
     * 중간에 나가도 세션이 예정/미완료 목록에 남는다 — [SessionRatingViewModel.save] 참고.
     */
    fun commitAndProceed() {
        saveNoteThen(leaveOnFailure = false) { it.copy(isSaving = false, isDone = true) }
    }

    /**
     * 뒤로가기. 적어둔 내용을 먼저 저장해서 다시 들어왔을 때 이어 쓸 수 있게 한다.
     *
     * 저장이 실패하거나 이미 다른 저장이 돌고 있어도 화면은 반드시 벗어난다 — 뒤로가기가
     * 막히면 앱을 강제 종료하는 것 말곤 빠져나갈 방법이 없다.
     */
    fun saveNoteAndExit() {
        if (_uiState.value.isSaving) {
            _uiState.update { it.copy(isExited = true) }
            return
        }
        saveNoteThen(leaveOnFailure = true) { it.copy(isSaving = false, isExited = true) }
    }

    /** [SessionActiveUiState.isExited] 소비 완료 신호. */
    fun onExitHandled() {
        _uiState.update { it.copy(isExited = false) }
    }

    private fun saveNoteThen(
        leaveOnFailure: Boolean,
        onSaved: (SessionActiveUiState) -> SessionActiveUiState,
    ) {
        // 연타로 저장이 겹치면 같은 내용이 여러 기록으로 생긴다.
        if (_uiState.value.isSaving) return
        // 확인 화면에서 기록이 삭제되면 flowState 가 비므로, 캐시해둔 id 도 함께 버린다.
        if (flowState.recordId == null) textRecordId = null
        val text = _uiState.value.noteText.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, recordErrorMessage = null) }
            try {
                val existingId = textRecordId
                when {
                    text.isNotEmpty() -> {
                        val record = if (existingId == null) {
                            addTextRecord(sessionId, text)
                        } else {
                            updateRecord(sessionId, existingId, text)
                        }
                        textRecordId = record.id
                        flowState.setRecord(record.id, record.contentText)
                    }
                    // 적어둔 걸 다 지웠으면 기록도 없앤다(서버가 빈 내용을 거부하기도 한다).
                    existingId != null -> {
                        deleteRecord(sessionId, existingId)
                        textRecordId = null
                        flowState.clearRecord()
                    }
                }
                _uiState.update(onSaved)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    if (leaveOnFailure) {
                        it.copy(isSaving = false, isExited = true)
                    } else {
                        it.copy(isSaving = false, recordErrorMessage = e.message)
                    }
                }
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
