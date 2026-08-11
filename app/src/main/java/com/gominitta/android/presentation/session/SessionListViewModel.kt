package com.gominitta.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.session.Session
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.model.worry.Worry
import com.gominitta.android.domain.usecase.GetSessionListUseCase
import com.gominitta.android.domain.usecase.GetWorriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
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
    private val getWorries: GetWorriesUseCase,
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
                // 세션의 worryTitle/worryContent는 생성 시점 스냅샷이라 걱정 수정이 반영 안 된다(백엔드 한계).
                // 완료 세션은 더 이상 수정할 일이 없어 스냅샷 그대로 두고, 예정/미완료만 최신 걱정 내용으로 덮어쓴다.
                val worryById = try {
                    when (val result = getWorries()) {
                        is ApiResult.Success -> result.data.associateBy(Worry::id)
                        else -> emptyMap()
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    emptyMap()
                }
                // 백엔드가 SCHEDULED → INCOMPLETE 자동 전환을 아직 구현 안 해서(배치 작업 없음),
                // 예약 종료 시각이 지났는데도 SCHEDULED로 남아있는 세션은 화면에서만 미완료로 취급한다.
                val now = LocalDateTime.now()
                val (overdueScheduled, stillScheduled) = sessions
                    .filter { it.status == SessionStatus.SCHEDULED }
                    .partition { it.scheduledEndAt.isBefore(now) }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasLoadedOnce = true,
                    scheduled = stillScheduled.withLiveWorryContent(worryById),
                    incomplete = (sessions.filter { it.status == SessionStatus.INCOMPLETE } + overdueScheduled)
                        .withLiveWorryContent(worryById),
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

    private fun List<Session>.withLiveWorryContent(worryById: Map<Long, Worry>): List<Session> = map { session ->
        val worry = worryById[session.worryId] ?: return@map session
        session.copy(worryTitle = worry.title, worryContent = worry.content)
    }
}
