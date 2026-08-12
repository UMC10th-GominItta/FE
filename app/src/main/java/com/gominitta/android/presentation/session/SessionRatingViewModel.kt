package com.gominitta.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.CompleteSessionUseCase
import com.gominitta.android.domain.usecase.StartSessionUseCase
import com.gominitta.android.presentation.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionRatingUiState(
    val emotionScore: Float = 5f,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,
)

@HiltViewModel
class SessionRatingViewModel @Inject constructor(
    private val completeSession: CompleteSessionUseCase,
    private val startSession: StartSessionUseCase,
    private val reminderScheduler: ReminderScheduler,
    private val flowState: SessionFlowState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionRatingUiState())
    val uiState: StateFlow<SessionRatingUiState> = _uiState.asStateFlow()

    fun updateScore(score: Float) {
        _uiState.update { it.copy(emotionScore = score) }
    }

    /**
     * "저장하기" 클릭. 서버가 in_progress 인 세션만 완료 처리해줘서 시작을 여기서 한다 —
     * 더 일찍 시작하면 도중에 나갔을 때 세션이 예정/미완료 목록에서 사라진다(목록 API가
     * in_progress 를 안 준다).
     */
    fun save() {
        val sessionId = flowState.sessionId
        if (sessionId == null) {
            _uiState.update { it.copy(errorMessage = "진행 중인 세션을 찾을 수 없어요.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                if (!flowState.isSessionStarted) {
                    startSession(sessionId)
                    flowState.markSessionStarted()
                }
                val session = completeSession(sessionId, _uiState.value.emotionScore.roundToInt())
                // 끝난 세션에 리마인드가 남아 뒤늦게 울리지 않도록 예약을 걷어낸다.
                reminderScheduler.cancelReminders(session.worryId)
                flowState.clear()
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }
}
