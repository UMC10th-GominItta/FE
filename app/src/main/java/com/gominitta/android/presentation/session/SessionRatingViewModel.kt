package com.gominitta.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.CompleteSessionUseCase
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
    private val flowState: SessionFlowState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionRatingUiState())
    val uiState: StateFlow<SessionRatingUiState> = _uiState.asStateFlow()

    fun updateScore(score: Float) {
        _uiState.update { it.copy(emotionScore = score) }
    }

    fun save() {
        val sessionId = flowState.sessionId
        if (sessionId == null) {
            _uiState.update { it.copy(errorMessage = "진행 중인 세션을 찾을 수 없어요.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                completeSession(sessionId, _uiState.value.emotionScore.roundToInt())
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
