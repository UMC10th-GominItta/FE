package com.gominitta.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.model.session.SessionSummary
import com.gominitta.android.domain.usecase.GetSessionListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SessionListUiState(
    val isLoading: Boolean = true,
    val scheduled: List<SessionSummary> = emptyList(),
    val incomplete: List<SessionSummary> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class SessionListViewModel @Inject constructor(
    getSessionList: GetSessionListUseCase,
) : ViewModel() {

    val uiState: StateFlow<SessionListUiState> = getSessionList()
        .map { sessions ->
            SessionListUiState(
                isLoading = false,
                scheduled = sessions.filter { it.status == SessionStatus.SCHEDULED },
                incomplete = sessions.filter { it.status == SessionStatus.INCOMPLETE },
            )
        }
        .catch { e -> emit(SessionListUiState(isLoading = false, errorMessage = e.message)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SessionListUiState())

    /** DataStore Flow가 저장소 변경에 자동으로 반응하므로 수동 재조회는 더 이상 필요 없다. */
    fun load() = Unit
}
