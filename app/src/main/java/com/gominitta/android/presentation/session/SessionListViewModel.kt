package com.gominitta.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.model.session.Session
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.usecase.GetSessionListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** 세션 목록 상단 탭 — raw 값 없이 라벨만 UI에서 그대로 쓴다. */
enum class SessionListTab(val label: String) {
    Scheduled("예정된 세션"),
    Incomplete("미완료 세션"),
    Completed("완료 세션"),
}

data class SessionListUiState(
    val isLoading: Boolean = true,
    val selectedTab: SessionListTab = SessionListTab.Scheduled,
    val scheduled: List<Session> = emptyList(),
    val incomplete: List<Session> = emptyList(),
    val completed: List<Session> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class SessionListViewModel @Inject constructor(
    private val getSessionList: GetSessionListUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionListUiState())
    val uiState: StateFlow<SessionListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val sessions = getSessionList()
                // 파라미터 없는 기본 목록엔 완료된 세션이 안 오므로 별도로 요청한다.
                // 서버가 아직 이 필터를 지원 안 하면(400) 완료 탭만 비워두고 나머지는 보여준다.
                val completedSessions = try {
                    getSessionList(SessionStatus.COMPLETED)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    emptyList()
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    scheduled = sessions.filter { it.status == SessionStatus.SCHEDULED },
                    incomplete = sessions.filter { it.status == SessionStatus.INCOMPLETE },
                    completed = completedSessions,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun selectTab(tab: SessionListTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}
