package com.gominitta.android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.NextSession
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.usecase.GetHomeUseCase
import com.gominitta.android.domain.usecase.GetSessionListUseCase
import com.gominitta.android.domain.usecase.GetWorriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHome: GetHomeUseCase,
    private val getSessionList: GetSessionListUseCase,
    private val getWorries: GetWorriesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val data = runCatching { getHome() }.getOrNull() ?: return@launch
            _uiState.value = HomeUiState(
                nickname = data.nickname,
                dailyMessage = data.dailyMessage,
                nextSession = loadNextActiveSession(),
                profileImageUrl = data.profileImageUrl,
            )
        }
    }

    /**
     * `/home`이 주는 nextSession 하나만 믿지 않는다 — 걱정을 삭제해도 백엔드가 연결된 세션을
     * 안 지워서(백엔드 한계) 그 삭제된 걱정의 세션이 시간상 가장 이르면 서버가 항상 그것부터
     * "다음 세션"으로 고른다. 그래서 세션 목록을 직접 받아 직접 고른다.
     *
     * 고르는 기준은 "예정된 세션 중 앞으로 가장 빨리 시작할 것" — 목록 API는 SCHEDULED와
     * INCOMPLETE를 같이 주는데, 이미 지나간 세션이 항상 시간상 제일 빠르므로 그대로 최솟값을
     * 잡으면 지난 세션만 계속 뽑힌다. 그래서 SCHEDULED이면서 아직 끝나지 않은 것만 후보로 둔다.
     */
    private suspend fun loadNextActiveSession(): NextSession? {
        val sessions = runCatching { getSessionList() }.getOrNull() ?: return null
        // null이면 걱정 목록 조회 자체가 실패한 것 — 이땐 필터링하지 않는다(실패를 전부
        // "삭제됨"으로 착각해서 다 걸러버리면 안 되므로).
        val activeWorries = when (val result = getWorries()) {
            is ApiResult.Success -> result.data.associateBy { it.id }
            is ApiResult.Error, is ApiResult.NetworkError -> null
        }
        val now = LocalDateTime.now()
        val session = sessions
            .filter { it.status == SessionStatus.SCHEDULED && it.scheduledEndAt.isAfter(now) }
            .filter { activeWorries == null || it.worryId in activeWorries }
            .minByOrNull { it.scheduledStartAt }
            ?: return null
        // 세션의 worryTitle은 생성 시점 스냅샷이라 걱정 수정이 반영 안 된다 — 최신 제목으로 덮어쓴다.
        return NextSession(
            sessionId = session.id,
            title = activeWorries?.get(session.worryId)?.title ?: session.worryTitle,
            status = session.status.raw,
            startedAt = session.scheduledStartAt,
        )
    }
}
