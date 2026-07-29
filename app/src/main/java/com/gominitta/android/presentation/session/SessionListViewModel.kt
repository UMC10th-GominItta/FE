package com.gominitta.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.model.session.SessionSummary
import com.gominitta.android.domain.usecase.GetSessionListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SessionListUiState(
    val isLoading: Boolean = true,
    val scheduled: List<SessionSummary> = emptyList(),
    val incomplete: List<SessionSummary> = emptyList(),
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
                _uiState.value = SessionListUiState(
                    isLoading = false,
                    scheduled = sessions.filter { it.status == SessionStatus.SCHEDULED },
                    incomplete = sessions.filter { it.status == SessionStatus.INCOMPLETE },
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}
