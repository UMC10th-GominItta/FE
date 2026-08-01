package com.gominitta.android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.model.session.SessionSummary
import com.gominitta.android.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val nextSession: SessionSummary? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: SessionRepository,
) : ViewModel() {

    /** 예정된 세션 중 scheduledStartAt이 가장 이른 것을 "다음 마음 세션"으로 보여준다. */
    val uiState: StateFlow<HomeUiState> = repository.getSessions(SessionStatus.SCHEDULED)
        .map { sessions -> HomeUiState(nextSession = sessions.minByOrNull { it.scheduledStartAt }) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
