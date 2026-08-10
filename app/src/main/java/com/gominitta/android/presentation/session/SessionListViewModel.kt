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
    /** 최초 로딩 성공 여부 — 이후 재조회(탭 재진입 등)는 이 화면을 스피너로 덮지 않고 조용히 갱신한다. */
    val hasLoadedOnce: Boolean = false,
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

    /**
     * 탭 재진입(ON_RESUME)마다 최신 상태를 반영하려고 매번 다시 부르지만, 첫 로딩 이후엔
     * 화면을 스피너로 덮지 않고 조용히 갱신한다 — 실패해도 이미 보여주던 목록은 그대로 둔다.
     */
    fun load() {
        viewModelScope.launch {
            val isFirstLoad = !_uiState.value.hasLoadedOnce
            if (isFirstLoad) {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            }
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
                    hasLoadedOnce = true,
                    scheduled = sessions.filter { it.status == SessionStatus.SCHEDULED },
                    incomplete = sessions.filter { it.status == SessionStatus.INCOMPLETE },
                    completed = completedSessions,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (isFirstLoad) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }

    fun selectTab(tab: SessionListTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}
